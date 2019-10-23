package fr.shingle.utils;

import java.util.stream.Stream;

public class StreamUtils {
    public static <T> Stream<T> concat(Stream<T>... streams) {
        switch (streams.length) {
            case 0:
                return Stream.empty();
            case 1:
                return streams[0];
            default:
                return Stream.of(streams).reduce(Stream::concat).get();
        }
    }
}
