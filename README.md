# iTransantiago

###Resumen
Aplicación iPhone para el cálculo y visualización de rutas de Transantiago, Paraderos y Puntos de Carga Bip.

###Licencia
Este software esta licenciado bajo la licencia BSD-3.

###Plataforma, Framework o Lenguaje de Programación / BBDD
Java

#FAQ
#####¿Cómo compilar?
1. Realizar clone local

2. Configurar url predicción de buses en clase `cl.gob.modernizacion.itransantiago.Config` ( nsilva@minsegpres.gob.cl )

3. Compilar usando gradle o Android Studio

#####Actualizar API KEY Google Maps
El API KEY sirve para contar un acceso correcto a la API Android de Google Maps.

Para ello se deben seguir los siguentes pasos

1. Crear keystore release. En Android Studio ir a `Build -> Generate Signed APK -> next `. Es importante tener clara ubicación del nuevo keystore `Key store path`, la primera vez se debe generar uno nuevo mediante la opción `Create new...`
2. Una vez creado un keystore de release podemos configurar en google. Para ello debemos ir a https://console.developers.google.com/project/itransantiago/apiui/credential
3. Navegar a través del menú `APIs y autenticación -> Credenciales -> Editar aplicaciones Android permitidas`
4. Como la página indicará se debe ejecutar el comnado `keytool -list -v -keystore mystore.keystore` donde mystore.keystore corresponde al keystore creado anteriormente.
5. Agregar al listado la línea `SHA1: E0:9A:BB...`
6. Al APK firmado será encontrado en la url `app/build/outputs/apk/app-release-unaligned.apk`

#####¿Cómo realizar una actualización de base de datos local?
La base de datos local contiene información de rutas de servicios, paraderos de servicios y puntos de recarga bip!
Esta base de datos se encuentra en el archivo `app/src/main/assets/databases/itransantiago.sqlite` y debe contener las siguientes tablas

* calendar
* frequencies
* puntobip
* routes
* shapes
* stop_times
* stops
* trips

La aplicación permite tener sólo un archivo de base de datos local a la vez. Esto quiere decir que cada cambio implica que la versión anterior sea reemplazada por la nueva versión.

Cada cambio en esta base de datos deberá ser indicado a su vez en el archivo `cl.gob.modernizacion.itransantiago.db.MyDatabase` en la variable `DATABASE_VERSION`
