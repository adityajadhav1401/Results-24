# Generated by Django 2.1.3 on 2018-12-26 19:54

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('website', '0009_auto_20181226_1908'),
    ]

    operations = [
        migrations.AddField(
            model_name='quizdetail',
            name='postDt',
            field=models.TextField(default='1-03-2018'),
            preserve_default=False,
        ),
    ]
