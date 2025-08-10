---
title: Conclusions and Retrospective
nav_order: 7
parent: Report
---

# Conclusions and Retrospective
### Development Process

The planned development process was followed closely, with weekly sprints that included a backlog and a final review. The Product Backlog was kept updated after each sprint.

Each sprint was completed on time and ended with a review meeting to assess progress and plan the next sprint. Almost all tasks assigned within each sprint were completed during that sprint.

Most of the work was done individually, except for some common parts that were designed and developed collaboratively. The same approach was taken for writing the documentation.

To assist with task and sprint management, we used the ClickUp web application, which helped us track progress and organize our work efficiently. You can view the project progress [here](https://app.clickup.com/90151320888/v/s/90155305539).


---

### Git Workflow

The GitFlow workflow was adopted, with branches used as follows:

- A **feature branch** for each feature, created and used only by the developer responsible for that task. Once finished, each was merged into the `develop` branch through a pull request.
- A **release branch** was created for each release, following Semantic Versioning. Each release branch was merged back into both `main` and `develop` via two separate pull requests.
- A **gh-pages** was created for write the documentation of the project.

Regarding **GitHub Actions**, these were set up before development began and greatly helped maintain continuous software integrity. This was achieved mainly through automatic testing on every push and consistent code style enforced by Scalafmt. Moreover, it enabled Continuous Delivery (CD) with automatic releases triggered by pushing semantic version tags (`v*.*.*`) on the `main` branch.

### Conclusions 
Overall, this project was challenging and insightful but very rewarding. We tried to keep high code quality and good teamwork all the time. Writing clean, reusable, and flexible code was not always easy, and writing tests early was also challenging. However, these difficulties taught us important lessons about creating real value. 
We learned that code can always be made better — cleaner, simpler, and more elegant — and that refactoring is necessary and should never be delayed. For this reason we are aware that the code can always be improved, especially regarding solid principles.
We used Scrum-like methods, with regular meetings and clear roles, which helped a lot with communication and coordination. Without this, we might have worked separately and had problems when combining our work or disagreements about the project direction.
