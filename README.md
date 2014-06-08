gunit-maven-plugin
==================

Geneate unit tests for antlr4 grammars from XML-Files.


Getting Started
===============

1. Checkout this project and install to your local maven repo:

    ```
    ➜ git clone https://github.com/areku/gunit-maven-plugin.git gunit
    ➜ cd gunit
    ➜ mvn install
    ```

2. Add following to the `pom.xml` of your project that want to use this plugin:
    
    ```xml
    <dependencies>
        <dependency>
            <groupId>weigl</groupId>
            <artifactId>gunit-maven-plugin</artifactId>
            <version>0.1</version>
        </dependency>
    </dependencies>
    ```
    
    ```xml
    
    <build>
     ... 
     <plugins>
         <plugin>
            <groupId>weigl</groupId>
                <artifactId>gunit-maven-plugin</artifactId>
                <version>0.1</version>
             </plugin>
          </plugin>
     ...
     </plugins>
    ```


3. Run this plugin: 
    
    ```➜ mvn weigl:gunit-maven-plugin:0.1:gunit -X```
    
