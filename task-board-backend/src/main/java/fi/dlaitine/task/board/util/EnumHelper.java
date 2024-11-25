package fi.dlaitine.task.board.util;

public class EnumHelper {

    /**
     * Maps an enum constant from one enum type to another based on their common values.
     *
     * @param <E>
     *     Enum type of the value to be mapped
     * @param <T>
     *     Enum type to which the value will be mapped
     * @param enum1
     *     Enum constant from the source enum type to be mapped
     * @param enum2Class
     *     Class object representing the destination enum type
     * @return mapped enum constant from the destination enum type
     * @throws IllegalArgumentException if the specified enum constant is not found in the destination enum type
     */
    public static <E extends Enum<E>, T extends Enum<T>> T mapEnum(E enum1, Class<T> enum2Class) throws IllegalArgumentException {
        return Enum.valueOf(enum2Class, enum1.name());
    }

}
