[![CircleCI](https://circleci.com/gh/BiBiServ/bibiserv/tree/development.svg?style=svg)](https://circleci.com/gh/BiBiServ/bibiserv/tree/development)

# Bibiserv

Web application framework mainly for bioinformatic developers to publish their tools with an user-friendly web interface.

## Development-Guidelines

https://github.com/BiBiServ/Development-Guidelines


### How to build the project?

dependency:  >= Maven 3.3.9 

Run

~~~BASH
mvn package
~~~

#### How to version the project?

We decided that all modules should have the same version as the parent module.
By using the below command in the project root you can update all child modules at once.

~~~BASH
mvn  versions:set -DnewVersion=<version>
~~~

where
  
 * version = **2.1.0.alpha.2**
