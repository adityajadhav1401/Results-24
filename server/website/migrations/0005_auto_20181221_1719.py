# Generated by Django 2.1.3 on 2018-12-21 17:19

from django.db import migrations


class Migration(migrations.Migration):

    dependencies = [
        ('website', '0004_auto_20181221_1707'),
    ]

    operations = [
        migrations.AlterUniqueTogether(
            name='jobresult',
            unique_together={('website', 'postDt', 'rctBrd', 'postNm', 'qual', 'advtNo', 'lastDt', 'detail')},
        ),
    ]
