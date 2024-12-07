## Микросервис registration_service, часть проекта Микросервисы, создаваемой в рамках мастерской Яндекса.
Проект разрабатывается совместно бывшими студентами ЯндексПрактикума.
### Авторы
- Горбачевым Иваном https://github.com/MrGriv
- Шаталова Ольга https://github.com/ol5ga
- Марина https://github.com/marina52746
- Михайлов Илья https://github.com/ism-ektb
- Шведов Алексей https://github.com/Aleksey01091993
### В проект входитят следующие микросервисы:
- user_service https://github.com/ism-ektb/user_service
- event_service https://github.com/ism-ektb/event_service
- task_service https://github.com/ism-ektb/task_service
- review_service https://github.com/ism-ektb/review_service
- registration_service https://github.com/ism-ektb/registration_service

На первом этапе сервисы работали независимо друг от друга. Сейчас в них добавляются новые функции по взаимодействию между собой для отработки практических навыков по разработке микросервисов

### Данный микросервис позволяет: 
- Создавать, редактировать и удалять заявки на регистрацию
- Одобрять регистрацию организаторами события. Для этого в регистрацию добавлено поле status, по умолчанию равное PENDING, и эндпоинт для обновления статуса заявки: отклонить (с указанием причины), одобрить, поставить в лист ожидания. Если заявка со статусом APPROVED удаляется, то заявка с самым ранним временем создания из листа ожидания переходит в PENDING.
- Есть возможность получения заявки по заданным статусам (одному или более) и по eventId, отсортированными по дате создания и получать количество заявок в разных статусах по eventId.
- при создании заявки на регистрацию создается новый пользователь в user_service, а присвоенный userId сохраняется в registration_service к соответствующей заявке. Если заявка удаляется, то удаляется и соответствующий пользователь.
- запрещено удалять свою APPROVED заявку, если мероприятие уже началось. Для этого делается запрос к event_service.
- сделана проверка на то, что пользователь регистрируется на мероприятие с открытой регистрацией. Для этого делается запрос к event_service
