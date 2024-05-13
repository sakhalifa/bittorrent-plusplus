#ifndef __THPOOL_H__
#define __THPOOL_H__

#include <pthread.h>
#include <semaphore.h>
#include "file.h"
#include "command.h"

typedef struct thread thread_t;
typedef struct thpool thpool_t;
typedef struct task_queue task_queue_t;
typedef struct task task_t;

typedef struct task_queue {
	int count;
	task_t *front;
	task_t *rear;
	pthread_mutex_t m_rw;
	sem_t has_task;
} task_queue_t;

typedef struct task {
	void *arg;
	int fd;
	char *(*p_func)(void *, struct file** , int*, struct peer*);
	task_t *prev;
} task_t;

typedef struct thread {
	int id;
	thpool_t* thpool;
	pthread_t p_thread;
	pthread_mutex_t m_thread;
} thread_t;

typedef struct thpool {
	int size;
	volatile int threads_alive;
	volatile int threads_working;
	thread_t **threads;
	task_queue_t queue;
	pthread_mutex_t m_count;
	pthread_cond_t threads_all_idle;
} thpool_t;

typedef struct arg {
	void* command;
	struct file** file;
	int* nb_file;
	struct peer* peer; 
} arg_t;

thpool_t *thpool_init(int num_threads);
void thpool_add_work(thpool_t *thpool, char *(*task)(void *, struct file** , int*, struct peer*), void *arg, int fd) ;
void thpool_destroy(thpool_t *thpool);
void thpool_wait(thpool_t *thpool);

// only for testing
task_queue_t *task_queue_init();
void task_queue_push(task_queue_t *q, task_t *t);
task_t *task_queue_pull(task_queue_t *q);
void task_queue_clear(task_queue_t *q);
void task_destroy(task_queue_t *q);
void thread_destroy(thread_t *thread);

#endif