Introduction
============

Calico is a tool to create and use Android Content Providers in a simple
manner.  It is an object-relational mapping originally inspired by ActiveRecord
from Ruby on Rails, but now very diverged because of the platform differences.

Content Providers are a useful tool whether or not you wish to share content
with other applications, but the the amount of boilerplate code necessary to
get one started is very off-putting.  Once ready, cursors are a efficient way
of accessing the content, but not very object-oriented.  This library aims to
deal with both these issues.


Quick Start
===========

To be written, once the first release is out. :P


Development
===========

If you want to contribute to the development, compile the library from source,
or simply add Calico as a submodule to your project, read this section.

Submodules
----------

This library depends in turn on other libraries.  Some are included as git
submodules; all those will be in the `submodules` directory.

After cloning the project, run `git submodules sync` and `git submodules
update` to pull the sources of those modules.

Building dependencies
---------------------

### Calico library

The Calico library depends on the Android 2.3.3 SDK and the Android
Compatibility package v4. It probably compiles with earlier versions of the
SDK, I just build it against level 10.

### Calico Android Unit tests

Same requirements as the library. Of course, it also depends on the library
itself. All unit and integration tests are in the

### Calico JUnit / Robolectric tests

To run tests, you need Robolectric and its dependencies.  Everything you need
should be available in the submodule directory.  The order of the dependencies
is important (i.e. JUnit4 should be before Android), the following one works:

1.  Robolectric main libraries (submodules/robolectric/lib/main/*)
2.  Robolectric test libraries (submodules/robolectric/lib/test/*)
3.  Robolectric
4.  Android SDK
5.  Android Compatibility package v4
6.  Calico sources

Note: Due to the low-level nature of testing Calico, Robolectric has proved
less helpful than I had hoped.  I've refactored most of the tests to Android
Unit tests, and will continue this trend.

### Robolectric

Robolectric is its own project, but its documentation is very much lacking.  To
build it the following dependency order works:

1.  Robolectric main libraries (submodules/robolectric/lib/main/*)
2.  Android SDK 2.3.3 with Google APIs (android-10)
3.  Android Compatibility package v4
4.  Java 1.6
5.  Robolectric sources

