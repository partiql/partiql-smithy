ROOT=.
OUT_DIR=$(ROOT)/build

html:
	asciidoctor -b html -D $(OUT_DIR) -R $(ROOT) './**/*.adoc'

clean:
	rm -r ./build

.PHONY: html clean
