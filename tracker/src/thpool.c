#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <sys/types.h>

#include "thpool.h"

typedef struct thread
{
    int thread_id;
    pthread_t *thread;
    thpool_t *thpool;
} thread_t;

typedef struct thpool
{
    int thread_alive;
    int thread_working;
    task_queue_t *task_queue;
    pthread_mutex_t m_count;
} thpool_t;

typedef struct task_queue
{
    int count;
    task_t *current;
    task_t *next;
    pthread_mutex_t m_rw;
} task_queue_t;

typedef struct task
{
    int task_id;
    void *p_func;
    task_t *next_task;
} task_t;

pthread_mutex_t m = PTHREAD_MUTEX_INITIALIZER;

static void *thread_function(thpool_t *thread_p)
{

    pthread_mutex_lock(&(thread_p->m_count));
    thread_p->thread_alive++;
    pthread_mutex_unlock(&(thread_p->m_count));

    // TODO

    pthread_mutex_lock(&(thread_p->m_count));
    thread_p->thread_alive--;
    pthread_mutex_unlock(&(thread_p->m_count));
}


static thread_t *thread_create(thpool_t *thpool, void *thread_work, int id)
{
    thread_t *thread = (thread_t *)malloc(sizeof(thread_t));
    thread->thpool = thpool;
    thread->thread_id = id;
    pthread_create((thread)->thread, NULL, thread_function, &(*thread));
    return thread;
}

thpool_t *thpool_init(int num_threads)
{
    thpool_t *thpool = (thpool_t *)malloc(sizeof(thpool));
    
    task_queue_init(&thpool->task_queue);

    for (int i = 0; i < num_threads; i++)
    {
        thread_create(thpool, thread_function, i);
    }

    pthread_mutex_init(&(thpool->m_count));

    return thpool;
}

void thpool_add_work(thpool_t *thpool, void (*task)(void *), void *arg);

void thpool_destroy(thpool_t *thpool);

static void thread_destroy (thread_t* thread){
	free(thread);
}

task_t *task_queue_pull(task_queue_t *q)
{
    pthread_mutex_lock(&(q->m_rw));
    task_t *t = q->current;

    if (q->count == 1)
    {
        q->count = 0;
        t = q->current;
        q->current = NULL;
    }
    if (q->count > 1 && q->current != NULL)
    {
        q->count--;
        t = q->current;
        q->current = q->next;
    }
    pthread_mutex_unlock(&(q->m_rw));
    return t;
}

task_queue_t *task_queue_init()
{
    task_queue_t *q = malloc(sizeof(task_queue_t));
    q->count = 0;
    q->current = NULL;
    q->next = NULL;

    pthread_mutex_init(&(q->m_rw), NULL);
    return q;
}

void task_queue_clear(task_queue_t *q)
{
    while (q->count != 0)
    {
        free(task_queue_pull(q));
    }

    q->count = 0;
    q->current = NULL;
    q->next = NULL;
}

void *task_destroy(task_queue_t *q)
{
    task_queue_clear(q);
    free(q);
}


void *task_queue_push(task_queue_t *q, task_t *t)
{
    pthread_mutex_lock(&(q->m_rw));
    if (q->count == 0)
    {
        q->current = t;
        q->next = NULL;
    }
    else
    {
        q->next->next_task = q->current;
        q->current = t;
    }
    q->count++;
    pthread_mutex_unlock(&(q->m_rw));

}
