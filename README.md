# Code Checker

A checker to check coding formatting and other rules of a java or c Project.

## Requirements

- Gradle
- Sdkman


## Install

1. Use the right tool versions by runnning

```shell
sdkman env install
```

2. Copy the checkers code
```shell
git clone https://github.com/Lukas-Kaufmann/code-checker.git
cd code-checker
```

3.Build the project

```shell
./gradlew :app:clean :app:distZip :app:distTar :app:installDist
```

4. Then add the created executable to the path

```shell
export PATH = "/path/to/project/app/build/install/code-checker/bin"
```

Then the code checker can be used in another project like this:

```shell
code-checker .
```

