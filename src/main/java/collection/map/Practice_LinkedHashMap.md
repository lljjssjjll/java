# LinkedHashMap

## 용도
- **삽입 순서를 유지**하는 키-값 맵 (HashMap + 순서)
- **LRU(Least Recently Used) 캐시** 구현 (`accessOrder=true`)
- API 응답처럼 **필드 순서가 중요**한 JSON 직렬화
- 내부: **HashMap 버킷 + 이중 연결 리스트** 구조  
  (삽입·접근 시 연결 리스트 포인터 갱신)

## 장점
| 항목 | 내용 |
|------|------|
| **삽입 순서 유지** | `for-each` 순회 시 삽입한 순서대로 반환 |
| **O(1) 연산** | HashMap과 동일하게 get/put/remove 평균 O(1) |
| **LRU 캐시** | `accessOrder=true` + `removeEldestEntry()` 오버라이드로 LRU 구현 |
| **예측 가능한 순회** | HashMap의 무작위 순서 문제 없음 |

## 단점
| 항목 | 내용 |
|------|------|
| **메모리 오버헤드** | 각 엔트리에 `before + after` 포인터 추가 |
| **삽입·삭제 추가 비용** | 연결 리스트 포인터 갱신 필요 (HashMap 대비 미미하게 느림) |
| **동기화 없음** | 멀티스레드 환경에서 외부 동기화 또는 `ConcurrentHashMap` 필요 |

## 성능

| 연산 | LinkedHashMap | HashMap | 비고 |
|------|--------------|---------|------|
| `get(k)` | O(1) ≈ | **O(1)** | `accessOrder=true`면 포인터 갱신 추가 |
| `put(k,v)` | O(1) ≈ | **O(1)** | 삽입 후 연결 리스트 포인터 갱신 |
| `remove(k)` | O(1) ≈ | **O(1)** | 연결 리스트 포인터 갱신 추가 |
| 삽입 순서 순회 | **O(n)** | X | 삽입 순서 보장 |
| 접근 순서 순회 | **O(n)** (accessOrder=true) | X | LRU 순서 |
| 메모리 사용 | 더 많음 | **적음** | `before/after` 포인터 추가 |

> **결론**: 순서 필요 없음 → HashMap / 삽입 순서 유지 → LinkedHashMap / LRU 캐시 → LinkedHashMap(accessOrder=true)
