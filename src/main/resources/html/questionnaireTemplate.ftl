<!DOCTYPE html>
<html>
<head>
    <title>Bootstrap Example</title>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css"/>
</head>

<body>
<div class="container">
    <h2>Вопрос</h2>
    <p>${text}</p>
    <form role="form" action="answer" method="post" accept-charset="utf-8">
        <input type="hidden" name="q" value="${num}"/>
        <#list answers as a>
        <div class="radio">
            <label><input type="radio" name="answer" value="${a}">${a}</label>
        </div>
        </#list>
    <button type="submit" class="btn btn-default">Отправить</button>
    </form>
</div>
</body>
</html>
