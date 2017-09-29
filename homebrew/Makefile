PREFIX ?= /usr/local
DESTDIR ?=

LIBPARAMTUNER = libparamtuner.dylib

HEADERS = FileSystemWatcher.hpp paramtuner.h osx/FSEventsFileSystemWatcher.hpp rapidxml-1.13/rapidxml_iterators.hpp rapidxml-1.13/rapidxml_print.hpp rapidxml-1.13/rapidxml_utils.hpp rapidxml-1.13/rapidxml.hpp

SOURCES = FileSystemWatcher.cpp paramtuner.cpp

CXX      ?= g++
CXXFLAGS := -mmacosx-version-min=10.7 -std=c++11 -stdlib=libc++
LIBS     = -framework CoreServices

OBJECTS = $(SOURCES:.cpp=.o)

all: lib/$(LIBPARAMTUNER)

lib/$(LIBPARAMTUNER): $(OBJECTS)
	$(CXX) -dynamiclib -install_name $(PREFIX)/lib/$(LIBPARAMTUNER) -o $@ $^ $(LDFLAGS) $(LIBS)

test/simpletest: test/simpletest.cpp
	$(CXX) $(CPPFLAGS) $(CXXFLAGS) -o $@ $^ $(LDFLAGS) $(LIBS) -lparamtuner

test: lib/$(LIBPARAMTUNER) test/simpletest
	test/simpletest
clean:
	rm -f $(OBJECTS)
distclean: clean
	rm -f lib/$(LIBPARAMTUNER)
install:
	mkdir -p $(PREFIX)/include
	cp paramtuner.h $(PREFIX)/include
	mkdir -p $(PREFIX)/lib
	cp -r lib $(PREFIX)
uninstall:
	rm -rf $(PREFIX)/include/paramtuner.h
	rm -f $(PREFIX)/lib/$(LIBPARAMTUNER)
tarball: distclean
	cd .. && tar cvzf libparamtuner-mac-1.1.tar.gz libparamtuner-1.1/
	openssl dgst -sha256 ../libparamtuner-mac-1.1.tar.gz
