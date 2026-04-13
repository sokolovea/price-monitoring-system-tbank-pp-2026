# price-monitoring-system-tbank-pp-2026
Project development workshop. Tbank, winter 2026.

# BTF SERVICE
Сразу скопированная ветка работать не будет, необходимо сгенерировать api при помощи gradle задач (core->tasks->openapi tools->openApiGenerate) полче чего в папке build появится шаблон

# NOTIFICATION SERVICE
# Ручная проверка и установка вебхука для телеграма
Установить вебхук бота: https://api.telegram.org/bot<bot_token>/setWebhook?url=<webhook_url>
Информация о вебхуке: https://api.telegram.org/bot<bot_token>/getWebhookInfo

# TgBotProperties
baseUrl - Домен сервера
webhookPath - путь к вебхуку который будет использовать телеграм
name - Имя бота
token - Токен бота

# CloudPub (Аналог ngrok)
Тк. телеграм требует сертификаты (https сайты) для локального запуска используется CloudPub - он создает туннели к нашему localhost, те приложение запущенное локально становится доступно из сети
В дальнейшем при развертывании на сервере надобность в этом отпадет. 
