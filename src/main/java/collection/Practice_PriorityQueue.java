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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

public class Practice_PriorityQueue {
    private static final int SIZE = 500_000;

    public static void main(String[] args) throws Exception {
        demonstratePurpose();
        demonstrateAdvantages();
        demonstrateDisadvantages();
        demonstratePerformance();
    }

    // 용도: 우선순위 기반 처리 — 항상 최솟값(기본) 또는 최댓값을 먼저 꺼냄
    static void demonstratePurpose() {
        System.out.println("=== [용도] PriorityQueue ===");

        // 기본: 자연 순서(오름차순, 최솟값 우선)
        PriorityQueue<Integer> minPQ = new PriorityQueue<>();
        minPQ.offer(5); minPQ.offer(1); minPQ.offer(3); minPQ.offer(2); minPQ.offer(4);
        System.out.println("삽입: [5,1,3,2,4]");
        System.out.print("poll 순서(최솟값 우선): ");
        while (!minPQ.isEmpty()) System.out.print(minPQ.poll() + " ");
        System.out.println();

        // 최댓값 우선 (역순 Comparator)
        PriorityQueue<Integer> maxPQ = new PriorityQueue<>(Comparator.reverseOrder());
        maxPQ.offer(5); maxPQ.offer(1); maxPQ.offer(3); maxPQ.offer(2); maxPQ.offer(4);
        System.out.print("poll 순서(최댓값 우선): ");
        while (!maxPQ.isEmpty()) System.out.print(maxPQ.poll() + " ");
        System.out.println();

        // 객체 우선순위 정렬
        record Task(String name, int priority) {}
        PriorityQueue<Task> taskPQ = new PriorityQueue<>(Comparator.comparingInt(Task::priority));
        taskPQ.offer(new Task("low",    3));
        taskPQ.offer(new Task("high",   1));
        taskPQ.offer(new Task("medium", 2));
        System.out.print("작업 처리 순서: ");
        while (!taskPQ.isEmpty()) System.out.print(taskPQ.poll().name() + " ");
        System.out.println();
        System.out.println();
    }

    // 장점: peek O(1) — 힙 루트가 항상 최솟값 / offer·poll O(log n)
    static void demonstrateAdvantages() {
        System.out.println("=== [장점] PriorityQueue ===");
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        for (int i = SIZE; i >= 1; i--) pq.offer(i);

        // O(1) 최솟값 조회 — 힙 루트 직접 참조
        long start = System.nanoTime();
        int min = pq.peek();
        long peekTime = System.nanoTime() - start;
        System.out.println("peek() 결과 : " + min + "  소요 시간: " + peekTime + " ns  (O(1))");

        // O(log n) poll
        start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) pq.offer(i);
        long offerTime = System.nanoTime() - start;
        System.out.println("offer() " + SIZE + "회 : " + offerTime / 1_000_000 + " ms  (O(log n))");
        System.out.println();
    }

    // 단점: 이터레이터 순회 시 정렬 순서 미보장 / contains·remove O(n) 선형 탐색
    static void demonstrateDisadvantages() {
        System.out.println("=== [단점] PriorityQueue ===");

        PriorityQueue<Integer> pq = new PriorityQueue<>(List.of(5, 1, 3, 2, 4));

        // iterator는 힙 내부 배열 순서(정렬 미보장)
        List<Integer> iterOrder = new ArrayList<>(pq);
        System.out.println("내부 배열 순서(iterator): " + iterOrder + "  (정렬 순서 아님)");
        System.out.println("peek()                  : " + pq.peek()    + "  (최솟값만 보장)");

        // contains/remove: 힙 전체 선형 탐색 → O(n)
        long start = System.nanoTime();
        boolean found = pq.contains(3);
        long elapsed = System.nanoTime() - start;
        System.out.println("contains(3) : " + found + "  (O(n) 선형 탐색)  " + elapsed + " ns");
        System.out.println();
    }

    // 성능: vs TreeSet — offer/poll 비교 (우선순위 큐 용도)
    static void demonstratePerformance() throws Exception {
        System.out.println("=== [성능] PriorityQueue vs TreeSet ===");
        Options opt = new OptionsBuilder()
                .include("Practice_PriorityQueue\\.PerformanceDemonstration")
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
        PriorityQueue<Integer> pq;
        TreeSet<Integer> ts;
        int idx;

        @Setup(Level.Trial)
        public void setup() {
            pq = new PriorityQueue<>(SIZE);
            ts = new TreeSet<>();
            for (int i = SIZE; i >= 1; i--) { pq.offer(i); ts.add(i); }
        }

        @Setup(Level.Iteration)
        public void resetIdx() { idx = 0; }

        @Benchmark
        public boolean priorityQueue_offer() {
            return pq.offer(idx++);
        }

        @Benchmark
        public boolean treeSet_add() {
            return ts.add(idx++);
        }

        @Benchmark
        public Integer priorityQueue_poll() {
            if (pq.isEmpty()) for (int i = SIZE; i >= 1; i--) pq.offer(i);
            return pq.poll();
        }

        @Benchmark
        public Integer treeSet_poll() {
            if (ts.isEmpty()) for (int i = SIZE; i >= 1; i--) ts.add(i);
            return ts.pollFirst();
        }
    }
}
