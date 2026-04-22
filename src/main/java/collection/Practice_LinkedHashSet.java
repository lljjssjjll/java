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

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Practice_LinkedHashSet {
    private static final int SIZE = 500_000;

    public static void main(String[] args) throws Exception {
        demonstratePurpose();
        demonstrateAdvantages();
        demonstrateDisadvantages();
        demonstratePerformance();
    }

    // 용도: 삽입 순서를 유지하면서 중복을 허용하지 않는 집합
    static void demonstratePurpose() {
        System.out.println("=== [용도] LinkedHashSet ===");

        LinkedHashSet<String> set = new LinkedHashSet<>();
        set.add("banana"); set.add("apple"); set.add("cherry"); set.add("date");
        System.out.println("삽입 순서 : banana → apple → cherry → date");
        System.out.println("순회 결과 : " + set + "  (삽입 순서 유지)");

        boolean added = set.add("apple");
        System.out.println("중복 추가(apple)     : " + added + "  (이미 존재, 순서 변경 없음)");
        System.out.println("중복 추가 후 순회    : " + set);

        // 방문 이력 추적 — 순서 있는 중복 없는 집합이 필요한 경우
        LinkedHashSet<String> visited = new LinkedHashSet<>();
        List<String> path = List.of("home", "about", "home", "contact", "about");
        for (String page : path) visited.add(page);
        System.out.println("방문 경로(중복 포함) : " + path);
        System.out.println("방문 이력(중복 제거) : " + visited);
        System.out.println();
    }

    // 장점: 삽입 순서 유지 + HashSet 수준의 O(1) add/contains/remove
    static void demonstrateAdvantages() {
        System.out.println("=== [장점] LinkedHashSet ===");
        LinkedHashSet<Integer> set = new LinkedHashSet<>(SIZE * 2);
        for (int i = 0; i < SIZE; i++) set.add(i);

        long start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) set.contains(i);
        long elapsed = System.nanoTime() - start;
        System.out.println("contains() " + SIZE + "회 : " + elapsed / 1_000_000 + " ms  (O(1) 평균, 순서 유지 상태)");
        System.out.println();
    }

    // 단점: HashSet 대비 메모리 오버헤드 — 각 버킷 엔트리에 prev·next 포인터 추가
    static void demonstrateDisadvantages() {
        System.out.println("=== [단점] LinkedHashSet ===");
        System.out.println("내부 구조: HashMap 버킷 엔트리 + 이중 연결 리스트(prev·next)");
        System.out.println("HashSet 엔트리  : [hash | key | value | next]");
        System.out.println("LinkedHashSet   : [hash | key | value | next | before | after]");
        System.out.println("→ 엔트리당 포인터 2개 추가 → HashSet 대비 메모리 증가");

        // 삽입 순서는 유지하지만 접근 순서(LRU 식) 변경은 지원 안 함
        LinkedHashSet<String> set = new LinkedHashSet<>();
        set.add("A"); set.add("B"); set.add("C");
        set.contains("A"); // 접근해도 순서 변화 없음
        System.out.println("A 접근 후 순서   : " + set + "  (접근 순서 재정렬 불가)");
        System.out.println();
    }

    // 성능: vs HashSet — add/contains 속도 비교 (LinkedHashSet이 미미하게 느림)
    static void demonstratePerformance() throws Exception {
        System.out.println("=== [성능] LinkedHashSet vs HashSet ===");
        Options opt = new OptionsBuilder()
                .include("Practice_LinkedHashSet\\.PerformanceDemonstration")
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
        HashSet<Integer>       hashSet;
        LinkedHashSet<Integer> linkedSet;
        int idx;

        @Setup(Level.Trial)
        public void setup() {
            hashSet   = new HashSet<>(SIZE * 2);
            linkedSet = new LinkedHashSet<>(SIZE * 2);
            for (int i = 0; i < SIZE; i++) { hashSet.add(i); linkedSet.add(i); }
        }

        @Setup(Level.Iteration)
        public void resetIdx() { idx = 0; }

        @Benchmark
        public boolean hashSet_contains() {
            return hashSet.contains(idx++ % SIZE);
        }

        @Benchmark
        public boolean linkedHashSet_contains() {
            return linkedSet.contains(idx++ % SIZE);
        }

        @Benchmark
        public boolean hashSet_add() {
            return hashSet.add(idx++ % SIZE);
        }

        @Benchmark
        public boolean linkedHashSet_add() {
            return linkedSet.add(idx++ % SIZE);
        }
    }
}
