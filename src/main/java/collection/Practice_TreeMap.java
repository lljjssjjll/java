package collection;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class Practice_TreeMap {
    private static final int SIZE = 500_000;

    public static void main(String[] args) throws Exception {
        demonstratePurpose();
        demonstrateAdvantages();
        demonstrateDisadvantages();
        demonstratePerformance();
    }

    // 용도: 키 자동 정렬 맵 / 범위 검색·최솟값·최댓값 키 조회
    static void demonstratePurpose() {
        System.out.println("=== [용도] TreeMap ===");

        TreeMap<Integer, String> map = new TreeMap<>();
        map.put(5, "E"); map.put(3, "C"); map.put(8, "H");
        map.put(1, "A"); map.put(9, "I"); map.put(2, "B");
        map.put(7, "G"); map.put(4, "D"); map.put(6, "F");
        System.out.println("삽입 순서: [5,3,8,1,9,2,7,4,6]");
        System.out.println("keySet()       : " + map.keySet()        + "  (자동 오름차순)");

        System.out.println("firstKey()     : " + map.firstKey()      + "  (최솟값 키)");
        System.out.println("lastKey()      : " + map.lastKey()       + "  (최댓값 키)");
        System.out.println("floorKey(6)    : " + map.floorKey(6)     + "  (6 이하 최댓값 키)");
        System.out.println("ceilingKey(6)  : " + map.ceilingKey(6)   + "  (6 이상 최솟값 키)");
        System.out.println("lowerKey(6)    : " + map.lowerKey(6)     + "  (6 미만 최댓값 키)");
        System.out.println("higherKey(6)   : " + map.higherKey(6)    + "  (6 초과 최솟값 키)");
        System.out.println("subMap(3,7)    : " + map.subMap(3, 7)    + "  (3 이상 7 미만)");
        System.out.println("headMap(5)     : " + map.headMap(5)      + "  (5 미만)");
        System.out.println("tailMap(5)     : " + map.tailMap(5)      + "  (5 이상)");

        NavigableMap<Integer, String> desc = map.descendingMap();
        System.out.println("descendingMap(): " + desc.keySet()       + "  (역순)");
        System.out.println();
    }

    // 장점: 항상 정렬 유지(Red-Black Tree) / 범위 뷰 subMap·headMap·tailMap 제공
    static void demonstrateAdvantages() {
        System.out.println("=== [장점] TreeMap ===");
        TreeMap<Integer, Integer> map = new TreeMap<>();
        for (int i = SIZE; i >= 1; i--) map.put(i, i); // 역순 삽입

        System.out.println("역순 삽입 후 firstKey() : " + map.firstKey() + "  (항상 최솟값)");
        System.out.println("역순 삽입 후 lastKey()  : " + map.lastKey()  + "  (항상 최댓값)");

        Map<Integer, Integer> range = map.subMap(1000, 2000);
        System.out.println("subMap(1000,2000) 크기  : " + range.size() + "  (범위 뷰, 별도 복사 없음)");
        System.out.println();
    }

    // 단점: 모든 연산 O(log n) — HashMap O(1) 대비 느림 / null 키 불가
    static void demonstrateDisadvantages() {
        System.out.println("=== [단점] TreeMap ===");

        TreeMap<Integer, Integer> map = new TreeMap<>();
        for (int i = 0; i < SIZE; i++) map.put(i, i);

        long start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) map.get(i);
        long elapsed = System.nanoTime() - start;
        System.out.println("get() " + SIZE + "회 : " + elapsed / 1_000_000 + " ms  (O(log n) 트리 탐색)");

        // null 키 불가
        try {
            map.put(null, -1);
        } catch (NullPointerException e) {
            System.out.println("null 키 삽입        : NullPointerException  (자연 순서 비교 불가)");
        }
        System.out.println();
    }

    // 성능: vs HashMap — put/get 속도 (HashMap O(1) vs TreeMap O(log n))
    static void demonstratePerformance() throws Exception {
        System.out.println("=== [성능] TreeMap vs HashMap ===");
        Options opt = new OptionsBuilder()
                .include("Practice_TreeMap\\.PerformanceDemonstration")
                .warmupIterations(2)
                .measurementIterations(3)
                .warmupTime(TimeValue.seconds(1))
                .measurementTime(TimeValue.seconds(1))
                .forks(1)
                .build();
        new Runner(opt).run();
    }

    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @State(Scope.Thread)
    public static class PerformanceDemonstration {
        TreeMap<Integer, Integer> treeMap;
        HashMap<Integer, Integer> hashMap;
        int idx;

        @Setup(Level.Trial)
        public void setup() {
            treeMap = new TreeMap<>();
            hashMap = new HashMap<>(SIZE * 2);
            for (int i = 0; i < SIZE; i++) { treeMap.put(i, i); hashMap.put(i, i); }
        }

        @Setup(Level.Iteration)
        public void resetIdx() { idx = 0; }

        @Benchmark
        public Integer treeMap_get() {
            return treeMap.get(idx++ % SIZE);
        }

        @Benchmark
        public Integer hashMap_get() {
            return hashMap.get(idx++ % SIZE);
        }

        @Benchmark
        public Integer treeMap_put() {
            int i = idx++ % SIZE;
            return treeMap.put(i, i);
        }

        @Benchmark
        public Integer hashMap_put() {
            int i = idx++ % SIZE;
            return hashMap.put(i, i);
        }
    }
}
