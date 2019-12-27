import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.testing.Test

/**
 * Task to touch all junit xml report files for all given {@link Test} {@code tasks}.
 * This is required due to Jenkins fails if test output is created before build starts which
 * could happen using Gradles up-to-date feature :(
 */
open class TouchTestResults : DefaultTask() {
    @InputFiles
    val tasks = mutableListOf<TaskProvider<Test>>()

    @TaskAction
    fun touch() {
        tasks.forEach { task ->
            val testResultsDir = task.get().reports.junitXml.destination
            if (testResultsDir.exists()) {
                val timestamp = System.currentTimeMillis()
                testResultsDir.listFiles()?.forEach { it.setLastModified(timestamp) }
            }
        }
    }

    fun tasks(vararg testTasks: TaskProvider<Test>) {
        tasks.addAll(testTasks)
        this.mustRunAfter(testTasks)
    }
}
