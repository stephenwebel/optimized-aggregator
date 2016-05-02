package SingleFileTest;

import org.springframework.util.StopWatch;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by stephenwebel1 on 5/1/16.
 */
public class SingleFileTest {

    public static final int TRIALS = 10;
    private static final List<Long> aggregateTime = new ArrayList<>();
    private static final List<Long> positionCreateTime = new ArrayList<>();

    private static class Position {
        public static final String[] ENTITIES = new String[]{"US", "UK", "JP", "IN"};
        protected String _entity;
        protected int _qty;

        public Position() {
            _entity = ENTITIES[ThreadLocalRandom.current().nextInt(0, ENTITIES.length)];
            _qty = ThreadLocalRandom.current().nextInt(0, 10000);
        }

        public String getEntity() {
            return _entity;
        }

        public int getQty() {
            return _qty;
        }

        @Override
        public String toString() {
            return "MultipleFileTest.Position [_entity=" + _entity + ", _qty=" + _qty + "]";
        }

    }

    private static class CollectorImplParC implements Collector<Position, long[], Long> {
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

    public static void main(String args[]) {
        try {
            SingleFileTest test = new SingleFileTest();
            StopWatch sw = new StopWatch();
            for (int i = 0; i < TRIALS; i++) {
                test.trial(sw);

                System.gc();
                System.gc();
            }
            System.out.println(sw.prettyPrint());

//            System.out.println("Number of trials: " + TRIALS);
//        System.out.println("Average Aggregate Time: "+ averageList(aggregateTime));
//        System.out.println("Average Creation Time: "+ averageList(positionCreateTime));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Map<String, Long>  trial(StopWatch sw) {


        final int positionCount = 10 * 1000000;

        sw.start("Create positions");
        List<Position> positionList = new ArrayList<>(positionCount);
        for (int i = 0; i < positionCount; i++) {
            positionList.add(new Position());
        }
        Position[] positionArray = positionList.toArray(new Position[positionList.size()]);
        sw.stop();
//        positionCreateTime.add(sw.getLastTaskTimeMillis());

        sw.start("Best Aggregate positions");

        Map<String, Long> aggr = aggregatePositionsAA_YC_AP_parallel_collector(positionArray, (p) -> p.getEntity(), positionList.size());
        sw.stop();

        aggr.get("");

        System.gc();
        System.gc();

        sw.start("Original Aggregate positions");

        aggr = aggregatePositions(positionList, (p) -> p.getEntity());
        sw.stop();

//        aggregateTime.add(sw.getLastTaskTimeMillis());
        return aggr;
    }


    public static Map<String, Long> aggregatePositionsAA_YC_AP_parallel_collector(Position[] positionList_,
                                                                                  Function<Position, String> groupBy_, int positionCount) {
        return Arrays.stream(positionList_).collect(Collectors.groupingBy(groupBy_, () -> new HashMap<>(positionCount), summingLongCustom(Position::getQty)));
    }

    public static Collector<Position, long[], Long> summingLongCustom(ToLongFunction<Position> mapper) {

        return new CollectorImplParC(() -> new long[1],
                (a, t) -> a[0] += mapper.applyAsLong(t),
                (a, b) -> {
                    a[0] += b[0];
                    return a;
                },
                a -> a[0],
                Collections.emptySet());
    }

    public static Map<String, Long> aggregatePositions(List<Position> positionList_, Function<Position, String> groupBy_) {
        Map<String, Long> aggrResults = new HashMap<>(Position.ENTITIES.length);

        for (Position p : positionList_) {
            aggrResults.compute(groupBy_.apply(p), (k, v) -> v == null ? p.getQty() : v + p.getQty());
        }

        return aggrResults;
    }

//    private static double averageList(List<Long> list) {
//
//        long sum = 0;
//        for (Long l : list)
//            sum += l;
//
//        return sum / list.size();
//    }
}
