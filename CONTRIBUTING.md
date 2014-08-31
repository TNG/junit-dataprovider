We love pull requests. Here is a quick guide:

1. You need to have a JDK (at least version 1.5) installed.
2. Fork the repo (see https://help.github.com/articles/fork-a-repo).
3. Create a new branch from master.
4. Ensure that you have a clean state by running `./gradlew clean build`.
5. Add your change together with a test (tests are not needed for refactorings and documentation changes).
6. *Format your code:* Only format the code you have changed.
7. Run `./gradlew clean build` again and ensure all tests are passing.
8. Push to your fork/branch and submit a pull request.

Now you are waiting on us. We review your pull request and at least leave some comments.

*Note:* If you are thinking of providing a fix for one of the bugs or feature requests, it is usually
a good idea to add a comment to the bug to make sure that there is agreement on how we should proceed.
