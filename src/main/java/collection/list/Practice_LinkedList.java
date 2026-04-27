package collection.list;

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
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class Practice_LinkedList {
    private static final int SIZE = 10_000;

    public static void main(String[] args) throws Exception {
        demonstratePurpose();
        demonstrateAdvantages();
        demonstrateDisadvantages();
        demonstratePerformance();
    }

    // 용도: 양 끝 삽입/삭제가 빈번하거나 Deque(스택·큐)로 활용
    static void demonstratePurpose() {
        System.out.println("=== [용도] LinkedList ===");

        // Deque 사용
        LinkedList<String> deque = new LinkedList<>();
        deque.addLast("B");
        deque.addFirst("A");
        deque.addLast("C");
        System.out.println("deque 구성          : " + deque);

        String head = deque.removeFirst();
        System.out.println("removeFirst()       : " + head + "  →  남은: " + deque);
        String tail = deque.removeLast();
        System.out.println("removeLast()        : " + tail + "  →  남은: " + deque);
        System.out.println("peekFirst()         : " + deque.peekFirst() + "  (제거 없음)");

        // 큐 사용 (FIFO)
        LinkedList<Integer> queue = new LinkedList<>();
        queue.offer(1); queue.offer(2); queue.offer(3);
        System.out.println("queue offer 1,2,3   : " + queue);
        System.out.println("queue poll()        : " + queue.poll() + "  →  남은: " + queue);

        // 스택 사용 (LIFO)
        LinkedList<Integer> stack = new LinkedList<>();
        stack.push(10); stack.push(20); stack.push(30);
        System.out.println("stack push 10,20,30 : " + stack);
        System.out.println("stack pop()         : " + stack.pop() + "  →  남은: " + stack);
        System.out.println();
    }

    // 장점: 양 끝 삽입/삭제 O(1) — 헤드·테일 포인터만 교체, 데이터 이동 없음
    static void demonstrateAdvantages() {
        System.out.println("=== [장점] LinkedList ===");
        LinkedList<Integer> list = new LinkedList<>();
        for (int i = 0; i < SIZE; i++) list.add(i);

        long start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) {
            list.addFirst(-i);   // 헤드 포인터 교체 → O(1)
            list.removeFirst();  // 헤드 포인터 교체 → O(1)
        }
        long elapsed = System.nanoTime() - start;
        System.out.println("addFirst+removeFirst " + SIZE + "회 : " + elapsed / 1_000_000 + " ms  (O(1) 반복)");
        System.out.println();
    }

    // 단점: 인덱스 접근 O(n) — 노드를 하나씩 따라가야 함 / prev·next 포인터 메모리 오버헤드
    static void demonstrateDisadvantages() {
        System.out.println("=== [단점] LinkedList ===");
        LinkedList<Integer> list = new LinkedList<>();
        for (int i = 0; i < SIZE; i++) list.add(i);

        // get(index): 중간 인덱스까지 노드를 순차 탐색 → O(n)
        long start = System.nanoTime();
        for (int i = 0; i < 10_000; i++) list.get(SIZE / 2);
        long elapsed = System.nanoTime() - start;
        System.out.println("get(중간 인덱스) 10,000회 : " + elapsed / 1_000_000 + " ms  (O(n) 탐색)");

        // 노드당 오브젝트 헤더 + 데이터 + prev + next 포인터 → ArrayList 대비 메모리 약 3배
        System.out.println("노드 구조: [prev | data | next]  →  ArrayList 배열 원소 대비 메모리 오버헤드");
        System.out.println();
    }

    // 성능: vs ArrayList — 맨 앞 삽입(LinkedList 우위) / 랜덤 접근(ArrayList 우위)
    static void demonstratePerformance() throws Exception {
        System.out.println("=== [성능] LinkedList vs ArrayList ===");
        Options opt = new OptionsBuilder()
                .include("Practice_LinkedList\\.PerformanceDemonstration")
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
        LinkedList<Integer> ll;
        ArrayList<Integer> al;
        int idx;

        @Setup(Level.Trial)
        public void setup() {
            ll = new LinkedList<>();
            al = new ArrayList<>(SIZE);
            for (int i = 0; i < SIZE; i++) { ll.add(i); al.add(i); }
        }

        @Setup(Level.Iteration)
        public void resetIdx() { idx = 0; }

        @Benchmark
        public int linkedList_get() {
            return ll.get(idx++ % SIZE);
        }

        @Benchmark
        public int arrayList_get() {
            return al.get(idx++ % SIZE);
        }

        @Benchmark
        public void linkedList_addFirst() {
            ll.addFirst(idx++);
            ll.removeLast();
        }

        @Benchmark
        public void arrayList_addFirst() {
            al.add(0, idx++);
            al.remove(al.size() - 1);
        }
    }
}
