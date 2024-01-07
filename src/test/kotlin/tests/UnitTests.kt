package tests

import data.Priority
import data.Task
import data.TasksRepository
import data.TasksRepositoryMemory
import io.github.serpro69.kfaker.Faker
import io.qameta.allure.Description
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class UnitTests {
    private lateinit var testRepository: TasksRepository
    private val faker = Faker()

    private fun createNewTask(repository: TasksRepository, faker: Faker): Int {
        val name = faker.funnyName.name()
        val priority = Priority.values().random()
        return repository.addTask(Task(name = name, priority = priority, completed = false))
    }

    @BeforeTest
    fun populateTasks() {
        val tasksRepositoryMemory = TasksRepositoryMemory()
        val iterations = faker.random.nextInt(2, 5)
        for (i in 1..iterations) {
            createNewTask(tasksRepositoryMemory, faker)
        }
        testRepository = tasksRepositoryMemory
    }

    @Test
    @Description("Добавление задачи и появление ее в списке")
    fun addTaskAndCheckAppearanceTest() {
        val newTaskId = createNewTask(testRepository, faker)
        val lastIdFromTaskList = testRepository.getTasks().last().id
        assertEquals(newTaskId, lastIdFromTaskList)
    }

    @Test
    @Description("Завершить задачу и проверить корректность работы фильтра по завершенным задачам")
    fun checkFinishFilterAfterFinishTaskTest() {
        val newTaskId = createNewTask(testRepository, faker)
        testRepository.completeTask(newTaskId)
        val sizeOfCompletedTasksList =
            testRepository.getTasks(completed = true).size - testRepository.getTasks(completed = false).size
        assertEquals(1, sizeOfCompletedTasksList, "Expected 1 task in filter, but got $sizeOfCompletedTasksList")
    }
}