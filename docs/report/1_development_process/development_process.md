---
title: Development Process
nav_order: 1
parent: Report
---

# Development Process

## Roles

**Ines Fraccalvieri** - domain expert and developer.

**Tiziano Vuksan** - product owner and developer.

## Sprints
### Sprint Duration

The **sprints** will have a weekly duration, from Monday to Sunday.

### Sprint planning and Daily Scrum

In each sprint planning, the objectives to be achieved will be defined, and through a **Sprint Backlog**, the tasks to be performed will be assigned to the various members.

Daily Scrum meetings under 15 minutes to coordinate tasks, share progress, and discuss problems.

### Sprint review and retrospective

During the sprint review and retrospective, we discussed the tasks that had been completed and identified those that needed to be postponed to the next sprint. We also considered possible changes to implement and updated the **Product Backlog** to prepare for the upcoming sprint.
## Definition of DONE

We consider a task or subtask "DONE":

*   For the **model** part of the project: the part of model involved in the "task" allows compilation and **must** pass all tests written while implementing it (using TDD methodology).
*   For the **view** part: the view **must be usable** (no pending buttons or empty "painting fields") and properly connected to the **model** (use TDD wherever possible**)**.

**In addition to this, there need to be ScalaDoc documentation.**

## Documentation

The documentation is created in **Markdown** format, contained in the `docs` directory, and published as **GitHub Pages**.

## Release workflow

We use Git by adopting the **GitFlow** workflow and the following strategy:

*   a `main` branch that contains the releases;
*   a `develop` branch that represents the main line of development;
*   a `feature` branch for each functionality.

Each implemented feature must be integrated into the `develop` branch through a **pull request**, which must be reviewed and approved by the other group member.

## Versioning

The versioning method used is Semantic Versioning, more specifically in the format:

`vMAJOR.MINOR.PATCH`

*   The `v` prefix is conventional but not required by Semantic Versioning itself.
*   `MAJOR`: Incremented for **breaking changes**.
*   `MINOR`: Incremented when you **add functionality** in a backward-compatible manner.
*   `PATCH`: Incremented for **backward-compatible bug fixes**.

  
## CI/CD Pipeline

The management and deployment of the project utilize Continuous Integration and Delivery techniques, specifically through GitHub Actions by creating workflows.

*   **Continuous Integration (CI):** The `test.yml` workflow automatically runs tests (Scalatest, Scoverage, and Scalafmt) on every push and pull request. This ensures the project's integrity throughout its development process.
*   **Continuous Delivery (CD):** The `release.yml` workflow is designed to automatically release the project only if all tests are successful. It is triggered by a push to the main branch with a semantic tag `v*.*.*` and produces multiple executable (one per OS: Windows, Linux, and macOS) JAR (`*.jar`) using sbt assembly, which is uploaded as a release on GitHub.