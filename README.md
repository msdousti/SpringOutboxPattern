# Spring Outbox Pattern

A proof-of-concept Spring project to save entity changes in an outbox table using Hibernate's [Interceptors and events](https://docs.jboss.org/hibernate/orm/5.6/userguide/html_single/Hibernate_User_Guide.html#events). The idea is fundamentally due to [Vlad Mihalcea's article](https://vladmihalcea.com/hibernate-event-listeners). The idea of how to make the integrator a Spring `@Component` so that injecting dependencies into them is possible is from [this StackOverflow post by codemonkey](https://stackoverflow.com/a/51522146/459391)

N.B.: This is impossible with pure JPA, as per [JPA specification](https://jakarta.ee/specifications/persistence/3.1/jakarta-persistence-spec-3.1.pdf#page=107), lifecycle methods should not access the EntityManager or other entities:

> In general, the lifecycle method of a portable application should not invoke EntityManager or query operations, access other entity instances, or modify relationships within the same persistence context. A lifecycle callback method may modify the non-relationship state of the entity on which it is invoked.
