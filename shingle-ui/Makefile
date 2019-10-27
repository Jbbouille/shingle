.PHONY: dist docker

dist:
	-rm -rf dist/
	npm run build

docker: dist
	docker build --no-cache -t shingle-ui .