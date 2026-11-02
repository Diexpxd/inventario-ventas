#!/bin/bash
# Crea el repositorio local con el flujo master / develop / feature/* y la etiqueta v1.0.0.
# Correrlo UNA vez desde Git Bash, dentro de esta carpeta:  bash flujo_git.sh
# Antes: git config --global user.name "Tu nombre" y user.email "tu correo" (si no estan puestos).
# Al final hay que agregar el remoto de GitHub y subir todo (ver el final del script).

set -e

if [ -d .git ]; then
    echo "Ya existe un repositorio aqui. No hago nada."
    exit 1
fi

git init -b master

git add .gitignore README.md
git commit -m "Commit inicial: README y .gitignore"

git checkout -b develop

nueva_rama() {
    git checkout -b "$1" develop
}

mezclar() {
    git checkout develop
    git merge --no-ff "$1" -m "Merge de $1 a develop"
}

nueva_rama feature/estructura-maven
git add pom.xml src/main/resources/application.properties
git commit -m "Estructura Maven: propiedades, dependencias y plugins"
mezclar feature/estructura-maven

nueva_rama feature/base-de-datos
git add src/main/resources/db/schema.sql
git commit -m "Esquema de la base de datos del Avance 1"
git add src/main/resources/db/datos.sql
git commit -m "Datos iniciales de categorias, proveedores y productos"
mezclar feature/base-de-datos

nueva_rama feature/modelo-y-excepciones
git add src/main/java/mx/tecmilenio/inventario/modelo src/main/java/mx/tecmilenio/inventario/dto
git commit -m "Modelo de dominio y objetos de peticion"
git add src/main/java/mx/tecmilenio/inventario/excepcion
git commit -m "Excepciones de la aplicacion y respuesta de error"
mezclar feature/modelo-y-excepciones

nueva_rama feature/repositorios-y-servicios
git add src/main/java/mx/tecmilenio/inventario/repositorio
git commit -m "Repositorios con JdbcTemplate"
git add src/main/java/mx/tecmilenio/inventario/servicio src/main/java/mx/tecmilenio/inventario/config/AppConfig.java src/main/java/mx/tecmilenio/inventario/config/DatosIniciales.java
git commit -m "Servicios con reglas de negocio, configuracion de datos y usuarios iniciales"
mezclar feature/repositorios-y-servicios

nueva_rama feature/seguridad-y-api-rest
git add src/main/java/mx/tecmilenio/inventario/seguridad
git commit -m "Autenticacion y permisos por rol"
git add src/main/java/mx/tecmilenio/inventario/api src/main/java/mx/tecmilenio/inventario/config/WebConfig.java src/main/java/mx/tecmilenio/inventario/config/WebAppInitializer.java
git commit -m "API REST en JSON y XML con hypermedia y manejo de errores"
mezclar feature/seguridad-y-api-rest

nueva_rama feature/interfaz-web
git add src/main/java/mx/tecmilenio/inventario/controlador
git commit -m "Controladores MVC y manejo de errores web"
git add src/main/webapp
git commit -m "Vistas JSP y estilos"
mezclar feature/interfaz-web

nueva_rama feature/pruebas-y-documentacion
git add src/test
git commit -m "Pruebas de la API con MockMvc"
git add -A
git commit -m "Documentacion: Javadoc y script del flujo de Git"
mezclar feature/pruebas-y-documentacion

git checkout master
git merge --no-ff develop -m "Version 1.0.0"
git tag -a v1.0.0 -m "Version final de la evidencia"

git checkout develop
echo
echo "Listo. Ramas:"
git branch
echo "Historial de develop:"
git log --oneline --graph develop | head -40
echo
echo "Falta subirlo a GitHub (crea antes el repositorio vacio):"
echo "  git remote add origin https://github.com/Diexpxd/inventario-ventas.git"
echo "  git push origin master develop --tags"
echo "  git push origin --all"
