# Generated by Django 2.1.3 on 2019-01-03 05:27

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('website', '0015_auto_20181227_1819'),
    ]

    operations = [
        migrations.CreateModel(
            name='Feedback',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('email', models.TextField()),
                ('message', models.TextField()),
            ],
        ),
        migrations.AddField(
            model_name='jobdetail',
            name='table',
            field=models.TextField(default=''),
            preserve_default=False,
        ),
    ]