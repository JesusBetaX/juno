package juno.util;

import static java.lang.Boolean.FALSE;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import static juno.util.Util.isNotNull;
import static juno.util.Util.isNull;

public final class Collect {
  
  private Collect() {}
  
  /**
   * Valida si una array esta vacia o es nulo.
   *
   * @param array a evaluar
   * @return boolean
   */
  public static <T> boolean isEmpty(T[] array) {
    return isNull(array) || array.length == 0;
  }
  
  public static boolean isEmpty(Collection it) {
    return isNull(it) || it.isEmpty();
  }
  
  public static boolean isEmpty(Map it) {
    return isNull(it) || it.isEmpty();
  }

  /**
   * Verifica si el índice existe en el array aunque sea nulo.
   *
   * @param array Un array con los índices para verificar
   * @param index índice para verificar.
   * @return @true si el array contiene el indice.
   */
  public static <T> boolean arrayHasIndex(T[] array, int index) {
    if (isNull(array)) return FALSE;
    return index > -1 && array.length > index;
  }
  
  public static <T> T arrayGet(T[] array, int index, T defaultVal) {
    return arrayHasIndex(array, index) ? array[index] : defaultVal;
  }
  
  public static <T> T arrayGet(T[] array, int index) {
    return arrayGet(array, index,  null);
  }

  /**
   * Devuelve un arreglo con los elementos dados.
   *
   * @param <T> tipo de array
   * @param elements `1, 2, 3, 4, 5, 6, 7`
   * @return </T> 
   */
  public static <T> T[] arrayOf(T... elements) { return elements; }

  public static void fill(StringBuilder out, Object... elements) {
    if (isNotNull(elements))
      for (Object e : elements) 
        out.append(e);
  }
  
  public static void fill(StringBuilder out, Collection elements) {
    if (isNotNull(elements))
      for (Object e : elements) 
        out.append(e);
  }
  
  public static <T> void fill(Collection<T> out, T... elements) { 
    if (isNotNull(elements))
      for (T e : elements) 
        out.add(e);
  }
  
  public static <K, V> void fill(Map<K, V> out, Object... namesAndValues) {
    if (isNull(namesAndValues)) return;
    for (int i = 0; i < namesAndValues.length; i += 2) {
      K name = (K) namesAndValues[i];
      V value = (V) namesAndValues[i + 1];
      out.put(name, value);
    }
  }
  
  /**
   * Devuelve una colección que aplica {@code function} a cada elemento de 
   * {@code fromCollection}.
   *
   * La colección devuelta es una vista en vivo de {@code fromCollection};
   * los cambios a uno afectan al otros.
   */
   public static <F, T> TransformedCollect<F, T> transform(
      Collection<F> fromCollection, Fun<? super F, T> function) {
    return new TransformedCollect<F, T>(fromCollection, function);
  }
  
  
  /**
   * Devuelve una nueva lista de elementos dados.
   *
   * @param <T> tipo de lista
   * @param elements `1, 2, 3, 4, 5, 6, 7`
   * @return </T> 
   */
  public static <T> ArrayList<T> listOf(T... elements) {
    if (isNull(elements)) return null;
    ArrayList<T> list = new ArrayList<T>(elements.length);
    fill(list, elements);
    return list;
  }

  /**
   * Devuelve un nuevo HashSet con los elementos dados.
   *
   * @param <T> tipo de lista
   * @param elements `1, 2, 3, 4, 5, 6, 7`
   * @return </T> 
   */
  public static <T> LinkedHashSet<T> setOf(T... elements) {
    if (isNull(elements)) return null;
    LinkedHashSet<T> list = new LinkedHashSet<T>(elements.length);
    fill(list, elements);
    return list;
  }

  /**
   * Devuelve un mapa con los contenidos especificados, dado como una lista de
   * pares donde el primer componente es la clave y el segundo es el valor.
   *
   * @param <K> tipo de clave
   * @param <V> tipo de valor
   * @param namesAndValues contenidos `"nombre", "jesus"`
   * @return HashMap</V></K> 
   */
  public static <K, V> LinkedHashMap<K, V> mapOf(Object... namesAndValues) {
    if (isNull(namesAndValues)) return null;
    if (namesAndValues.length % 2 != 0) {
      throw new IllegalArgumentException("Expected alternating header names and values");
    }
    LinkedHashMap<K, V> map = new LinkedHashMap<K, V>(namesAndValues.length / 2);
    fill(map, namesAndValues);
    return map;
  }
  
  /**
   * Corta una lista en sublistas segun la longitud establecida.
   * 
   * @param list lista a cortar.
   * @param len longitud para las sublistas
   * @return 
   */
  public static <V> List<List<V>> partition(List<V> list, int len) {
    List<List<V>> parts = new ArrayList<List<V>>();
    final int size = list.size();
    for (int i = 0; i < size; i += len) {
      parts.add(new ArrayList<V>(
              list.subList(i, Math.min(size, i + len))
      ));
    }
    return parts;
  }
  
  public static <V> void reverse(final V[] array, final int startIndexInclusive, final int endIndexExclusive) {
    if (array == null) {
      return;
    }
    int i = startIndexInclusive < 0 ? 0 : startIndexInclusive;
    int j = Math.min(array.length, endIndexExclusive) - 1;
    V tmp;
    while (j > i) {
      tmp = array[j];
      array[j] = array[i];
      array[i] = tmp;
      j--;
      i++;
    }
  }
  public static <V> void reverse(final V[] array) {
    reverse(array, 0, array.length);
  }
  
  public static List<String> toListString(List list, Fun<Object, String> fun) {
    if (isNull(list)) return null;
    List<String> result = new ArrayList<String>(list.size());
    for (int i = 0; i < list.size(); i++)  result.add( fun.apply(list.get(i)) );
    return result;
  }
  public static List<String> toListString(List list) {
    return toListString(list, Fun.OBJ_TO_STR);
  }
  
  public static String[] toArrayString(Object[] array, Fun<Object, String> fun) {
    if (isNull(array)) return null;
    String[] result = new String[array.length];
    for (int i = 0; i < array.length; i++)  result[i] = fun.apply(array[i]);
    return result;
  }
  public static String[] toArrayString(Object... array) {
    return toArrayString(array, Fun.OBJ_TO_STR);
  }
  
  /**
   * <p>Une todos los elementos de una colección en una cadena.</p>
   * 
   * @param args elementos
   * @param separator delimitador entre elementos
   * @param fun función ha aplicar para cada elemento
   * @return String
   */
  public static String joinToStr(Iterable args, String separator, Fun<Object, String> fun) {
    StringBuilder sb = new StringBuilder();
    int i = 0;
    for (Object arg : args) {
      if (i > 0) sb.append(separator);
      sb.append(fun.apply(arg));
      i++;
    }
    return sb.toString();
  }
  public static String joinToStr(Iterable args, String separator) {
    return joinToStr(args, separator, Fun.OBJ_TO_STR);
  }
  public static String joinToStr(Iterable args, Fun<Object, String> fun) {
    return joinToStr(args, ", ", fun);
  }
  public static String joinToStr(Iterable args) {
    return joinToStr(args, ", ");
  }
  
  /**
   * <p>Une todos los elementos de un arreglo en una cadena.</p>
   * 
   * @param args elementos
   * @param separator delimitador entre elementos
   * @param fun función ha aplicar para cada elemento
   * @return String
   */
  public static String joinToStr(Object[] args, String separator, Fun<Object, String> fun) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < args.length; i++) {
      if (i > 0) sb.append(separator);
      sb.append(fun.apply(args[i]));
    }
    return sb.toString();
  }
  public static String joinToStr(Object[] args, String separator) {
    return joinToStr(args, separator, Fun.OBJ_TO_STR);
  }
  public static String joinToStr(Object[] args, Fun<Object, String> fun) {
    return joinToStr(args, ", ", fun);
  }
  public static String joinToStr(Object[] args) {
    return joinToStr(args, ", ");
  }
}
