ROOT=.
OUT_DIR=$(ROOT)/build

html:
	asciidoctor -b html -D $(OUT_DIR) -a stylesheet=stylesheet.css *.adoc

clean:
	rm -r ./build

.PHONY: html clean
