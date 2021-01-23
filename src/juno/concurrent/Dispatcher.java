package juno.concurrent;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import juno.Platform;

public final class Dispatcher implements ThreadFactory {
  private static Dispatcher instance;

  /** Livera las respuestas al hilo de la UI. */
  private Executor executorDelivery;
  
  /** Ejecuta las llamadas "Call". */
  private ExecutorService executorService;
  
  public Dispatcher(ExecutorService executorService) {
    this.executorService = executorService;
  }

  public Dispatcher() {
  }
  
  public synchronized static Dispatcher get() {
    if (instance == null) {
      instance = new Dispatcher();
    }
    return instance;
  }
  
  @Override public Thread newThread(Runnable runnable) {
    Thread result = new Thread(runnable, "juno Dispatcher");
    result.setPriority(Thread.MIN_PRIORITY);
    return result;
  }
  
  public synchronized ExecutorService executorService() {
    if (executorService == null) {
      int nThreads = 1; //4
      executorService = new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
          new LinkedBlockingQueue<Runnable>(), this);
    }
    return executorService;
  }
  public void setExecutorService(ExecutorService es) {
    executorService = es;
  }
  
  public static <V> AsyncCall<V> callUserfun(final Object obj, final String method, final Object... params) {
    Dispatcher dispatcher = Dispatcher.get(); 
    return new AsyncCall<V>(dispatcher) {
      @Override 
      public V doInBackground() throws Exception {
        Class<?>[] types = new Class<?>[params.length];
        for (int i = 0; i < params.length; i++) {
          types[i] = params[i].getClass();
        }
        java.lang.reflect.Method instanceMethod = obj.getClass().getMethod(method, types);
        return (V) instanceMethod.invoke(obj, params);
      }
    };
  }
  
  /** 
   * Crea una llamada. 
   * @param task tarea propuesta para la ejecución.
   */
  public static <V> AsyncCall<V> newCall(final Task<V> task) {
    Dispatcher dispatcher = Dispatcher.get(); 
    return new AsyncCall<V>(dispatcher) {
      @Override 
      public V doInBackground() throws Exception {
        return task != null ? task.doInBackground() : null;
      }
    };
  }
  
  /** Ejecuta la llamada en la cola de peticiones. */
  public synchronized void execute(AsyncCall<?> task) { 
    if (task.isCancelled() || task.isDone()) return;
    // Propone una tarea Runnable para la ejecución y devuelve un Futuro.
    task.future = executorService().submit(task); 
  }
    
  public Executor executorDelivery() {
    if (executorDelivery == null) {
      executorDelivery = Platform.get();
    }
    return executorDelivery;
  }
  public void setExecutorDelivery(Executor executor) {
    executorDelivery = executor;
  }
  
  /**
   * Metodo que se encarga de liverar la respuesta obtenida, al hilo de la UI.
   */
  public <V> void onResponse(final Callback<V> callback, final V result) {
    executorDelivery().execute(new Runnable() {  
      @Override public void run() {
        try {
          callback.onResponse(result);
        } catch (Exception error) {
          callback.onFailure(error);
        }
      }
    });
  }

  /**
   * Metodo que se encarga de liverar el error obtenido, al hilo de la UI.
   */
  public void onFailure(final Callback<?> callback, final Exception error) {
    executorDelivery().execute(new Runnable() {
      @Override public void run() {
        callback.onFailure(error);
      }
    });
  }

}