#Upepeo Club

The file structure is critical to the functioning of the app

    \ext

        upepeo.sqlite
    \out
        \production
            \job_one
                App.class
    \src
        \META-INF
            MANIFEST.mf
        App.java
        Database.java
        Include.java
        Setup.java
        BorrowVideo.java
        BorrowVideo.form
        FinanceManager.java
        FinanceManager.form
        LandingPage.java
        LandingPage.form
        MemberRegister.java
        MemberRegister.form
        VideoRegister.java
        VideoRegister.form
    \res
        sqlite-jdbc-3.34.0.jar

#Don't

1. Don't move files carelessly

2. Don't delete any of the files including the .form files

3. Don't delete any file in the out directory

Upepeo.db is the database file

**sqlite-jdbc-3.34.0.jar** is the DB-connector 

#Usage

Only run the App.class within the out directory

To run the file

    java  -classpath <absolute-path-of-"sqlite-jdbc-3.34.0.jar"> <absolute-path-of-App.class_


Files generated for reports are in the ext directory