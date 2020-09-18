Чтобы собрать с исходников, нужны два токена mapbox. [Смотрите документацию](https://docs.mapbox.com/android/maps/overview/).

Секретный токен надо добавить в local.properties в корне проекта, вот так:

```
MAPBOX_DOWNLOADS_TOKEN=dsfafsdfsd
```

Второй, который для отображения карты, надо добавить в куда-нибудь в values в ресурсах:

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
	<string name="mapbox_access_token">afdsafdsafds</string>
</resources>
```