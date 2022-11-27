# Spring Outbox Pattern

A proof-of-concept Spring project to save entity changes in an outbox table using Hibernate's Event Listeners. The idea is fundamentally due to [Vlad Mihalcea's article](https://vladmihalcea.com/hibernate-event-listeners).

N.B.: This is impossible with pure JPA, as per [JPA specification](https://jakarta.ee/specifications/persistence/3.1/jakarta-persistence-spec-3.1.pdf#page=107), lifecycle methods should not access the EntityManager or other entities:

> In general, the lifecycle method of a portable application should not invoke EntityManager or query operations, access other entity instances, or modify relationships within the same persistence context. A lifecycle callback method may modify the non-relationship state of the entity on which it is invoked.