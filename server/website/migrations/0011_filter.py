# Generated by Django 2.1.3 on 2018-12-27 17:34

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('website', '0010_quizdetail_postdt'),
    ]

    operations = [
        migrations.CreateModel(
            name='Filter',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('title', models.TextField()),
                ('subtitle', models.TextField()),
            ],
        ),
    ]