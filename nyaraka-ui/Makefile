.PHONY: dist docker

dist:
	-rm -rf dist/
	gobble build dist/

docker: dist
	docker build --no-cache -t registry.build.libon.com/libon/nyaraka-ui .