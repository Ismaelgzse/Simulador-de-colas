# Aplicación *Simulador de la teoría de colas*

En esta aplicación web cualquier persona podrá experimentar creando, personalizando y simulando sistemas de colas. El Simulador de la teoría de colas se trata de una aplicación cuyos principales objetivos es la observación y estudio de las líneas de espera o sistemas de colas, permitiendo la mejor comprensión de las leyes que Agner Krarup Erlang desarrolló en 1909. El usuario podrá tanto simular un experimento en tiempo real como en tiempo acelerado, permitiéndole además en esta última opción la exportación de los datos obtenidos una vez finalizada la simulación.

## Uso e instalación
Debido a que esta aplicación web no está alojada en ningún servidor. La forma más rápida de poder empezar a utilizar la aplicación es mediante Docker. Docker es una herramienta que permite empaquetar y desplegar aplicaciones de forma rápida. Permitiendo que cualquier usuario sin importar su sistema operativo pueda hacer uso de esta aplicación únicamente teniendo instalado docker engine o docker desktop.

Hay dos formas de empezar a utilizar la aplicación:

### Mediante Docker Compose

>Prerrequisitos:
>- Tener instalado [Docker Desktop](https://www.docker.com/products/docker-desktop/), que es la opción recomendable, o tener instalado [Docker Engine](https://docs.docker.com/engine/install/) y [Docker Compose](https://docs.docker.com/compose/install/).

Esta es la forma más rápida y sencilla de empezar a usar la aplicación. Estos son los pasos a seguir:
1. Descargar el archivo llamado [docker-compose.yml](https://github.com/Ismaelgzse/Simulador-de-colas/blob/main/docker-compose.yml) que se encuentra en la raíz del repositorio.
2.  Abrir la consola del ordenador y apuntar a la carpeta o directorio en el que se encuentra el archivo descargado.
3. Escribir el siguiente comando en la terminal:
`docker compose up -d`
4. Una vez terminado el proceso de construcción de los contenedores, se podrá acceder en la siguiente url: [https://127.0.0.1:8443/app/](https://127.0.0.1:8443/app/)
5. Para parar la aplicación bastará con poner en la terminal el siguiente comando:
`docker compose down`

### Mediante Dockerfile
Usando este método, además de poder correr la aplicación, podrás crear tu propia imagen de la aplicación en caso de querer modificar el código fuente.

>Prerrequisitos:
>- Tener instalado [Docker Desktop](https://www.docker.com/products/docker-desktop/), que es la opción recomendable, o tener instalado [Docker Engine](https://docs.docker.com/engine/install/) y [Docker Compose](https://docs.docker.com/compose/install/).
>- Tener una base de datos [Mysql](https://www.mysql.com/downloads/) descargada, y un esquema de datos llamado **sistemacolas**.
>- Tener el código de la aplicación que se encuentra en el repositorio descargado.

Estos son los pasos a seguir:
1. Una vez descargado el código de la aplicación, abrir la consola apuntando a la raíz del proyecto.
	> **Nota**: Se considera raíz del proyecto donde están los archivos Dockerfile, docker-compose.yml, y las carpetas de backend y frontend de la aplicación.
	
2. Ejecutar el siguiente comando en la consola: 
`docker build -t nombre_imagen_deseado .`
	> **Nota**: En **nombre_imagen_deseado** podrás poner el nombre que desees a la imagen.
3. Una vez construida la imagen, ejecutar el siguiente comando en la consola para correr la aplicación:
`docker run -p 8443:8443 nombre_imagen_deseado`
4. Se podrá acceder a la aplicación en la siguiente url:
[https://127.0.0.1:8443/app/](https://127.0.0.1:8443/app/)
5. Para parar la aplicación pulsa `Ctrl + C`