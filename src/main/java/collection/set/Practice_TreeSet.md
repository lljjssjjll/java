# TreeSet

## 용도
- **자동 정렬된 집합** 유지 (삽입 시 자동 정렬)
- **최솟값·최댓값** 조회 (`first()`, `last()`)
- **범위 검색** (`subSet`, `headSet`, `tailSet`)
- **근사값 탐색** (`floor`, `ceiling`, `lower`, `higher`)
- 내부: **Red-Black Tree** (TreeMap 기반) 구현

## 장점
| 항목 | 내용 |
|------|------|
| **항상 정렬 유지** | 삽입 즉시 트리 재조정, 별도 정렬 불필요 |
| **범위 뷰 O(log n)** | `subSet/headSet/tailSet` 원본을 공유하는 뷰 반환 (복사 없음) |
| **근사 탐색 O(log n)** | `floor(x)` = x 이하 최댓값, `ceiling(x)` = x 이상 최솟값 |
| **역순 순회** | `descendingSet()` / `descendingIterator()` 제공 |

## 단점
| 항목 | 내용 |
|------|------|
| **모든 연산 O(log n)** | 트리 탐색·재조정으로 HashSet O(1) 대비 느림 |
| **null 삽입 불가** | 자연 순서 Comparator 사용 시 `NullPointerException` |
| **메모리 오버헤드** | 노드당 `left + right + parent + color` 포인터 |
| **재조정 비용** | 삽입·삭제 시 Red-Black Tree 회전 발생 가능 |

## 성능

| 연산 | TreeSet | HashSet | 비고 |
|------|---------|---------|------|
| `add(e)` | O(log n) | **O(1) 평균** | 트리 탐색·재조정 vs 해시 버킷 접근 |
| `contains(e)` | O(log n) | **O(1) 평균** | 트리 탐색 vs 해시 버킷 접근 |
| `remove(e)` | O(log n) | **O(1) 평균** | 트리 삭제·재조정 vs 해시 버킷 제거 |
| `first()/last()` | **O(1)** | X | 트리 가장 왼쪽/오른쪽 노드 |
| `subSet(a,b)` | **O(log n)** | X | 뷰 반환, 복사 없음 |
| `floor/ceiling` | **O(log n)** | X | HashSet에 없는 기능 |

> **결론**: 빠른 조회·중복 제거만 필요 → HashSet / 정렬·범위·최솟값·최댓값 필요 → TreeSet
