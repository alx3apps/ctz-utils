Various Java utility classes
============================

About utilities
---------------

This project contains various utilities, created when working with Spring Framework, Guava, PotgreSQL and others.
I doubt that somebody may need all this project. But code snippets from these utilities, that were collected over the years and finally organized in well-tested (JUnit tests included) and documented (not now :) project, might be helpful.

I'm going to document all of the utilities, but it will take time.

Note (__named-regexp__): classes form package ru.concerteza.util.namedregex weren't written by me. They were copied from here http://code.google.com/p/named-regexp/ without changes (only package name changed) because I wasn't able to find them in public maven repo's.

Building
--------

Only non-maven-central dependency is https://github.com/alx3apps/jgit-buildnumber
After that standard maven build should work:

    mvn clean install

