## Installation

1.  Clone the repo `git clone https://github.com/5g-media/All-in-one-ui-backend.git ~/sdk/all-in-one-ui-backend/`
2.  Go to your project folder `cd ~/sdk/all-in-one-ui-backend/`
3.  Clone the cno repo `git clone --single-branch --branch sdk https://github.com/mkheirkhah/5gmedia.git 5gmedia-cno`
4.  Install maven `sudo apt install -y maven`
5.  Create a java package `mvn -Dmaven.test.skip=true package`
6.  Start the package at the background `nohup java -jar target/all-in-one-ui-backend-0.0.1-SNAPSHOT.jar &` 