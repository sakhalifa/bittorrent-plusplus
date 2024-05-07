#ifndef __THPOOL_H__
#define __THPOOL_H__

typedef struct thread thread_t;
typedef struct thpool thpool_t;
typedef struct task_queue task_queue_t;
typedef struct task task_t;

typedef struct task_queue {
	int count;
	task_t *front;
	task_t *rear;
	pthread_mutex_t m_rw;
} task_queue_t;

typedef struct task {
	void *arg;
	void *(*p_func)(void *arg);  /* function pointer */
	task_t *prev;
} task_t;

typedef struct thread {
	int id;
	pthread_t p_thread;
	pthread_mutex_t m_thread;
} thread_t;

typedef struct thpool {
	int size;
	thread_t **threads;
	task_queue_t queue;
} thpool_t;

thpool_t *thpool_init(int num_threads);
void thpool_add_work(thpool_t *thpool, void *(*task)(void *), void *arg) ;
void thpool_destroy(thpool_t *thpool);
void thread_destroy(thread_t *thread);

// remove this before release
task_queue_t *task_queue_init();
void task_queue_push(task_queue_t *q, task_t *t);
task_t *task_queue_pull(task_queue_t *q);
void task_queue_clear(task_queue_t *q);
void task_destroy(task_queue_t *q);
void thread_destroy(thread_t *thread);

#endif