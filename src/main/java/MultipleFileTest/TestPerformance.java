package MultipleFileTest;

import org.springframework.util.StopWatch;

import java.util.*;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by stephenwebel1 on 4/27/16.
 */
public class TestPerformance {

    public static final int TRIALS = 100;
//    private static final List<Long> aggregateTime = new ArrayList<>();
//    private static final List<Long> positionCreateTime = new ArrayList<>();

    public static void main(String args[]) {
        try {
            TestPerformance test = new TestPerformance();
            StopWatch sw = new StopWatch();
            for (int i = 0; i < TRIALS; i++) {
                test.trial(sw);
                System.gc();
                System.gc();
            }
            System.out.println(sw.prettyPrint());

//            System.out.println("Number of trials: " + TRIALS);
//            System.out.println("Average Aggregate Time: " + averageList(aggregateTime));
//            System.out.println("Average Creation Time: " + averageList(positionCreateTime));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void trial(StopWatch sw) {


        final int positionCount = 10 * 1000000;

        sw.start("Create positions");
        List<Position> positionList = new ArrayList<>(positionCount);
        for (int i = 0; i < positionCount; i++) {
            positionList.add(new Position());
        }
        Position[] positionArray = positionList.toArray(new Position[positionList.size()]);
        sw.stop();
//        positionCreateTime.add(sw.getLastTaskTimeMillis());

        sw.start("Aggregate positions");
//        Map<String,Long> aggr = aggregatePositions(positionList,(p)->p.getEntity());

        aggregatePositionsAA_YC_AP_parallel_collector(positionArray, (p) -> p.getEntity(), positionList.size());
        sw.stop();

//        aggregateTime.add(sw.getLastTaskTimeMillis());
    }


    public static Map<String, Long> aggregatePositionsAA_YC_AP_parallel_collector(Position[] positionList_,
                                                                                  Function<Position, String> groupBy_, int positionCount) {
        return Arrays.stream(positionList_).parallel()
                .collect(Collectors.groupingBy(groupBy_, () -> new HashMap<>(positionCount), summingLongCustom(Position::getQty)));
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
//
//    public static Collector<MultipleFileTest.Position, long[], Long> summingLongCustom(ToLongFunction<MultipleFileTest.Position> mapper) {
//        return new MultipleFileTest.CollectorImplParC(() -> new long[1], (a, t) -> {
//            a[0] += mapper.applyAsLong(t);
//        }, (a, b) -> {
//            a[0] += b[0];
//            return a;
//        }, a -> a[0], Collections.emptySet());
//    }

    public static Map<String, Long> aggregatePositions(List<Position> positionList_, Function<Position, String> groupBy_) {
        Map<String, Long> aggrResults = new HashMap<String, Long>(Position.ENTITIES.length);

        for (Position p : positionList_) {
            aggrResults.compute(groupBy_.apply(p), (k, v) -> v == null ? p.getQty() : v + p.getQty());
        }

        return aggrResults;
    }

    private static double averageList(List<Long> list) {

        long sum = 0;
        for (Long l : list)
            sum += l;

        return sum / list.size();
    }

}