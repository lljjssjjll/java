package collection;

import java.util.ArrayDeque;
import java.util.LinkedList;

public class Practice_ArrayDeque {
    private static final int SIZE = 500_000;

    public static void main(String[] args) {
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
    static void demonstratePerformance() {
        System.out.println("=== [성능] ArrayDeque vs LinkedList(Deque) ===");

        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>(SIZE);
        LinkedList<Integer> linkedDeque = new LinkedList<>();

        // offer + poll 교차 반복
        long start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) arrayDeque.offerLast(i);
        for (int i = 0; i < SIZE; i++) arrayDeque.pollFirst();
        long arrayTime = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) linkedDeque.offerLast(i);
        for (int i = 0; i < SIZE; i++) linkedDeque.pollFirst();
        long linkedTime = System.nanoTime() - start;

        System.out.printf("offerLast+pollFirst %,d회 → ArrayDeque: %,d ns  LinkedList: %,d ns%n",
                SIZE, arrayTime, linkedTime);

        // addFirst + removeFirst
        ArrayDeque<Integer> ad2 = new ArrayDeque<>(SIZE);
        LinkedList<Integer> ll2 = new LinkedList<>();

        start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) ad2.addFirst(i);
        for (int i = 0; i < SIZE; i++) ad2.removeFirst();
        long adFirst = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) ll2.addFirst(i);
        for (int i = 0; i < SIZE; i++) ll2.removeFirst();
        long llFirst = System.nanoTime() - start;

        System.out.printf("addFirst+removeFirst %,d회 → ArrayDeque: %,d ns  LinkedList: %,d ns%n",
                SIZE, adFirst, llFirst);
        System.out.println("→ LinkedList는 노드 객체 할당·GC 비용 / ArrayDeque는 배열 인덱스 이동만");
        System.out.println();
    }
}
