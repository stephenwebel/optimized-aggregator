package MultipleFileTest;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Created by stephenwebel1 on 4/28/16.
 */
//public class MultipleFileTest.CollectorImplParC implements Collector<MultipleFileTest.Position, long[], Long> {
//    private final Supplier<long[]> supplier;
//    private final BiConsumer<long[], MultipleFileTest.Position> accumulator;
//    private final BinaryOperator<long[]> combiner;
//    private final Function<long[], Long> finisher;
//    private final Set<Characteristics> characteristics;
//
//    MultipleFileTest.CollectorImplParC(Supplier<long[]> supplier, BiConsumer<long[], MultipleFileTest.Position> accumulator,
//                      BinaryOperator<long[]> combiner, Function<long[], Long> finisher,
//                      Set<Characteristics> characteristics) {
//        this.supplier = supplier;
//        this.accumulator = accumulator;
//        this.combiner = combiner;
//        this.finisher = finisher;
//        this.characteristics = characteristics;
//    }
//
//    @Override
//    public BiConsumer<long[], MultipleFileTest.Position> accumulator() {
//        return accumulator;
//    }
//
//    @Override
//    public Supplier<long[]> supplier() {
//        return supplier;
//    }
//
//    @Override
//    public BinaryOperator<long[]> combiner() {
//        return combiner;
//    }
//
//    @Override
//    public Function<long[], Long> finisher() {
//        return finisher;
//    }
//
//    @Override
//    public Set<Characteristics> characteristics() {
//        return characteristics;
//    }
//
//    private static <I> Function<I, Long> castingIdentity() {
//        return i -> (Long) i;
//    }
//}

public class CollectorImplParC implements Collector<Position, long[], Long> {
    private final Supplier<long[]> supplier;
    private final BiConsumer<long[], Position> accumulator;
    private final BinaryOperator<long[]> combiner;
    private final Function<long[], Long> finisher;
    private final Set<Characteristics> characteristics;

    CollectorImplParC(Supplier<long[]> supplier, BiConsumer<long[], Position> accumulator,
                      BinaryOperator<long[]> combiner, Function<long[], Long> finisher,
                      Set<Characteristics> characteristics) {
        this.supplier = supplier;
        this.accumulator = accumulator;
        this.combiner = combiner;
        this.finisher = finisher;
        this.characteristics = characteristics;
    }

    CollectorImplParC(Supplier<long[]> supplier, BiConsumer<long[], Position> accumulator,
                      BinaryOperator<long[]> combiner, Set<Characteristics> characteristics) {
        this(supplier, accumulator, combiner, castingIdentity(), characteristics);
    }

    @Override
    public BiConsumer<long[], Position> accumulator() {
        return accumulator;
    }

    @Override
    public Supplier<long[]> supplier() {
        return supplier;
    }

    @Override
    public BinaryOperator<long[]> combiner() {
        return combiner;
    }

    @Override
    public Function<long[], Long> finisher() {
        return finisher;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return characteristics;
    }

    private static <I> Function<I, Long> castingIdentity() {
        return i -> (Long) i;
    }
}