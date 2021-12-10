# Contributing

Contributions are very welcome. The following will provide some helpful guidelines.


## JUnit dataprovider Contributor License Agreement

* You will only submit contributions using your real name (sorry, no pseudonyms or anonymous contributions).
* You will only submit contributions where you have authored 100% of the content.
* You will only submit contributions to which you have the necessary rights.
This means in particular, that if you are employed you have received the necessary permissions
from your employer to make the contributions.
* Whatever content you contribute will be provided under the project license(s) (see [LICENSE](LICENSE))


## Accept Developer Certificate of Origin

In order for your contributions to be accepted, you must [sign off](https://git-scm.com/docs/git-commit#git-commit---signoff)
your Git commits to indicate that you agree to the terms of [Developer Certificate of Origin](https://developercertificate.org/).


## Follow the Code of Conduct

Contributors must follow the Code of Conduct outlined at [CODE-OF-CONDUCT.md](CODE-OF-CONDUCT.md).


## How to contribute

We love pull requests. Here is a quick guide:

1. You need to have a JDK (at least version 1.8) installed.
2. Fork the repo (see https://help.github.com/articles/fork-a-repo).
3. Create a new branch from `main`.
4. Ensure that you have a clean state by running `./gradlew clean build`.
5. Add your change together with a test (tests are not needed for refactorings and documentation changes).
6. Run `./gradlew clean build` again and ensure all tests are passing.
7. Push to your fork/branch and submit a pull request.
8. Add the following to your Pull Request description:
```
---

I hereby agree to the terms of the JUnit dataprovider Contributor License Agreement.
```
9. Now you are waiting on us. We review your pull request and at least leave some comments.

*Note:* If you are thinking of providing a fix for one of the bugs or feature requests, it is usually
a good idea to add a comment to the bug to make sure that there is agreement on how we should proceed.
