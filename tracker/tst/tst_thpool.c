#include <assert.h>
#include <stdio.h>
#include <stdlib.h>

#include "thpool.h"
#include "tst.h"

static int pool_size = 8;

void test_task_queue_init() {
	printf("\t%s", __func__);
	task_queue_t *q = task_queue_init();
	assert(q != NULL);
	free(q);
	printf("\tOK\n");
}

void test_task_queue_push() {
	printf("\t%s", __func__);
	task_queue_t *q = task_queue_init();

	task_t *t1 = malloc(sizeof(task_t));
	t1->arg    = NULL;
	t1->p_func = NULL;
	t1->prev   = NULL;

	task_t *t2 = malloc(sizeof(task_t));
	t2->arg    = NULL;
	t2->p_func = NULL;
	t2->prev   = t1;

	task_queue_push(q, t1);

	assert(q->front == t1);
	assert(q->rear == t1);
	assert(q->count == 1);

	task_queue_push(q, t2);

	assert(q->front == t1);
	assert(q->rear == t2);
	assert(q->count == 2);

	free(t1);
	free(t2);
	free(q);
	printf("\tOK\n");
}

void test_task_queue_pull() {
	printf("\t%s", __func__);
	task_queue_t *q = task_queue_init();

	task_t *t1 = malloc(sizeof(task_t));
	t1->arg    = NULL;
	t1->p_func = NULL;
	t1->prev   = NULL;

	task_queue_push(q, t1);

	assert(q->front == t1);
	assert(q->rear == t1);
	assert(q->count == 1);

	task_t *t2 = task_queue_pull(q);

	assert(q->front == NULL);
	assert(q->rear == NULL);
	assert(q->count == 0);

	assert(t2 == t1);

	free(t1);
	free(q);
	printf("\tOK\n");
}

void test_task_queue_clear() {
	printf("\t%s", __func__);
	task_queue_t *q = task_queue_init();

	task_t *t1 = malloc(sizeof(task_t));
	t1->arg    = NULL;
	t1->p_func = NULL;
	t1->prev   = NULL;

	task_t *t2 = malloc(sizeof(task_t));
	t2->arg    = NULL;
	t2->p_func = NULL;
	t2->prev   = NULL;

	task_queue_push(q, t1);
	task_queue_push(q, t2);

	assert(q->front == t1);
	assert(q->rear == t2);
	assert(q->count == 2);

	task_queue_clear(q);

	assert(q->front == NULL);
	assert(q->rear == NULL);
	assert(q->count == 0);

	free(q);
	printf("\tOK\n");
}

void test_thpool_init() {
	printf("\t%s", __func__);
	thpool_t *thpool = thpool_init(pool_size);

	assert(thpool->size == pool_size);
	assert(&thpool->queue != NULL);
	assert(thpool->threads != NULL);

	thpool_destroy(thpool);
	printf("\tOK\n");
}

void* mock_command(void *arg) {
	return NULL;
}

void test_thpool_add_work() {
	printf("\t%s", __func__);
	thpool_t *thpool = thpool_init(pool_size);

	thpool_add_work(thpool, mock_command, NULL);

	assert(thpool->queue.count == 1);
	assert(thpool->queue.rear->p_func == mock_command);
	assert(thpool->queue.front->p_func == mock_command);

	thpool_destroy(thpool);
	printf("\tOK\n");
}