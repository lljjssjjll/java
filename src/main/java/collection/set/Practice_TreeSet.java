package collection.set;

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
import java.util.HashSet;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

public class Practice_TreeSet {
    private static final int SIZE = 500_000;

    public static void main(String[] args) throws Exception {
        demonstratePurpose();
        demonstrateAdvantages();
        demonstrateDisadvantages();
        demonstratePerformance();
    }

    // 용도: 자동 정렬 집합 / 범위 검색·최솟값·최댓값 조회
    static void demonstratePurpose() {
        System.out.println("=== [용도] TreeSet ===");

        TreeSet<Integer> set = new TreeSet<>(List.of(5, 3, 8, 1, 9, 2, 7, 4, 6));
        System.out.println("삽입 원소      : [5, 3, 8, 1, 9, 2, 7, 4, 6]");
        System.out.println("TreeSet 순회   : " + set + "  (자동 오름차순 정렬)");

        System.out.println("first()        : " + set.first()     + "  (최솟값)");
        System.out.println("last()         : " + set.last()      + "  (최댓값)");
        System.out.println("floor(6)       : " + set.floor(6)    + "  (6 이하 최댓값)");
        System.out.println("ceiling(6)     : " + set.ceiling(6)  + "  (6 이상 최솟값)");
        System.out.println("lower(6)       : " + set.lower(6)    + "  (6 미만 최댓값)");
        System.out.println("higher(6)      : " + set.higher(6)   + "  (6 초과 최솟값)");
        System.out.println("subSet(3,7)    : " + set.subSet(3, 7)   + "  (3 이상 7 미만)");
        System.out.println("headSet(5)     : " + set.headSet(5)     + "  (5 미만)");
        System.out.println("tailSet(5)     : " + set.tailSet(5)     + "  (5 이상)");
        System.out.println("descendingSet(): " + set.descendingSet() + "  (역순)");
        System.out.println();
    }

    // 장점: 항상 정렬 유지(Red-Black Tree) / 범위 연산 O(log n)
    static void demonstrateAdvantages() {
        System.out.println("=== [장점] TreeSet ===");
        TreeSet<Integer> set = new TreeSet<>();
        for (int i = SIZE; i >= 1; i--) set.add(i); // 역순 삽입

        System.out.println("역순 삽입 후 first() : " + set.first() + "  (항상 최솟값)");
        System.out.println("역순 삽입 후 last()  : " + set.last()  + "  (항상 최댓값)");

        SortedSet<Integer> range = set.subSet(100, 200);
        System.out.println("subSet(100,200) 크기 : " + range.size() + "  (범위 뷰, 별도 복사 없음)");
        System.out.println();
    }

    // 단점: 모든 연산 O(log n) — HashSet의 O(1) 대비 느림 / null 삽입 불가(Comparator 없을 때)
    static void demonstrateDisadvantages() {
        System.out.println("=== [단점] TreeSet ===");

        TreeSet<Integer> set = new TreeSet<>();
        for (int i = 0; i < SIZE; i++) set.add(i);

        long start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) set.contains(i);
        long elapsed = System.nanoTime() - start;
        System.out.println("contains() " + SIZE + "회 : " + elapsed / 1_000_000 + " ms  (O(log n) — 트리 탐색)");

        // null 삽입 불가
        try {
            set.add(null);
        } catch (NullPointerException e) {
            System.out.println("null 삽입 시도      : NullPointerException  (자연 순서 비교 불가)");
        }
        System.out.println();
    }

    // 성능: vs HashSet — add/contains 속도 (HashSet O(1) vs TreeSet O(log n))
    static void demonstratePerformance() throws Exception {
        System.out.println("=== [성능] TreeSet vs HashSet ===");
        Options opt = new OptionsBuilder()
                .include("Practice_TreeSet\\.PerformanceDemonstration")
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
        TreeSet<Integer> treeSet;
        HashSet<Integer> hashSet;
        int idx;

        @Setup(Level.Trial)
        public void setup() {
            treeSet = new TreeSet<>();
            hashSet = new HashSet<>(SIZE * 2);
            for (int i = 0; i < SIZE; i++) { treeSet.add(i); hashSet.add(i); }
        }

        @Setup(Level.Iteration)
        public void resetIdx() { idx = 0; }

        @Benchmark
        public boolean treeSet_contains() {
            return treeSet.contains(idx++ % SIZE);
        }

        @Benchmark
        public boolean hashSet_contains() {
            return hashSet.contains(idx++ % SIZE);
        }

        @Benchmark
        public boolean treeSet_add() {
            return treeSet.add(idx++ % SIZE);
        }

        @Benchmark
        public boolean hashSet_add() {
            return hashSet.add(idx++ % SIZE);
        }
    }
}
