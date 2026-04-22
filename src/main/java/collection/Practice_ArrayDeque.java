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
import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class Practice_ArrayDeque {
    private static final int SIZE = 500_000;

    public static void main(String[] args) throws Exception {
        demonstratePurpose();
        demonstrateAdvantages();
        demonstrateDisadvantages();
        demonstratePerformance();
    }

    // 용도: 스택·큐·덱 구현 / 양 끝 O(1) 삽입·삭제
    static void demonstratePurpose() {
        System.out.println("=== [용도] ArrayDeque ===");

        // 큐(FIFO) 사용
        ArrayDeque<String> queue = new ArrayDeque<>();
        queue.offer("first"); queue.offer("second"); queue.offer("third");
        System.out.println("queue 구성    : " + queue);
        System.out.println("poll()        : " + queue.poll() + "  →  남은: " + queue);

        // 스택(LIFO) 사용
        ArrayDeque<String> stack = new ArrayDeque<>();
        stack.push("A"); stack.push("B"); stack.push("C");
        System.out.println("stack 구성    : " + stack);
        System.out.println("pop()         : " + stack.pop() + "  →  남은: " + stack);

        // 덱(양방향) 사용
        ArrayDeque<Integer> deque = new ArrayDeque<>();
        deque.addFirst(2); deque.addFirst(1);
        deque.addLast(3);  deque.addLast(4);
        System.out.println("deque 구성    : " + deque);
        System.out.println("peekFirst()   : " + deque.peekFirst());
        System.out.println("peekLast()    : " + deque.peekLast());
        System.out.println("pollFirst()   : " + deque.pollFirst() + "  →  남은: " + deque);
        System.out.println("pollLast()    : " + deque.pollLast()  + "  →  남은: " + deque);
        System.out.println();
    }

    // 장점: 원형 배열 구조로 양 끝 O(1) / 연속 메모리로 LinkedList 대비 캐시 효율 우수
    static void demonstrateAdvantages() {
        System.out.println("=== [장점] ArrayDeque ===");
        ArrayDeque<Integer> deque = new ArrayDeque<>(SIZE);

        long start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) deque.offerLast(i);
        for (int i = 0; i < SIZE; i++) deque.pollFirst();
        long elapsed = System.nanoTime() - start;

        System.out.println("offerLast+pollFirst " + SIZE + "회 : " + elapsed / 1_000_000 + " ms  (O(1) 원형 배열)");
        System.out.println("→ 원형 배열: head/tail 인덱스만 이동, 데이터 복사 없음");
        System.out.println();
    }

    // 단점: null 원소 허용 안 함 / 인덱스 직접 접근 불가
    static void demonstrateDisadvantages() {
        System.out.println("=== [단점] ArrayDeque ===");

        ArrayDeque<String> deque = new ArrayDeque<>();

        // null 삽입 불가
        try {
            deque.offer(null);
        } catch (NullPointerException e) {
            System.out.println("null 삽입 시도    : NullPointerException  (null 허용 안 함)");
        }

        // 인덱스 직접 접근 API 없음
        deque.offer("A"); deque.offer("B"); deque.offer("C");
        System.out.println("deque 구성         : " + deque);
        System.out.println("peek()/peekFirst() : " + deque.peek() + "  (앞 원소만 조회 가능)");
        System.out.println("→ get(index) 없음 — 중간 원소 접근 불가, 전체 순회 필요");
        System.out.println();
    }

    // 성능: vs LinkedList(Deque) — offer/poll 반복 (ArrayDeque 우위: 캐시 친화 원형 배열)
    static void demonstratePerformance() throws Exception {
        System.out.println("=== [성능] ArrayDeque vs LinkedList(Deque) ===");
        Options opt = new OptionsBuilder()
                .include("Practice_ArrayDeque\\.PerformanceDemonstration")
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
        ArrayDeque<Integer> arrayDeque;
        LinkedList<Integer> linkedList;
        int idx;

        @Setup(Level.Trial)
        public void setup() {
            arrayDeque = new ArrayDeque<>();
            linkedList = new LinkedList<>();
        }

        @Setup(Level.Iteration)
        public void resetIdx() { idx = 0; }

        @Benchmark
        public Integer arrayDeque_offerPoll() {
            arrayDeque.offerLast(idx++);
            return arrayDeque.pollFirst();
        }

        @Benchmark
        public Integer linkedList_offerPoll() {
            linkedList.offerLast(idx++);
            return linkedList.pollFirst();
        }

        @Benchmark
        public void arrayDeque_addFirstRemoveFirst() {
            arrayDeque.addFirst(idx++);
            arrayDeque.removeFirst();
        }

        @Benchmark
        public void linkedList_addFirstRemoveFirst() {
            linkedList.addFirst(idx++);
            linkedList.removeFirst();
        }
    }
}
