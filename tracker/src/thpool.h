#ifndef __THPOOL_H__
#define __THPOOL_H__

typedef struct thread thread_t;
typedef struct thpool thpool_t;
typedef struct task_queue task_queue_t;
typedef struct task task_t;

thpool_t *thpool_init(int num_threads);
void thpool_add_work(thpool_t *thpool, void (*task)(void *), void *arg);
void thpool_destroy(thpool_t *thpool);

#endif