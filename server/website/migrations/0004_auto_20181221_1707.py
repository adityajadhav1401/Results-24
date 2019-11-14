# Generated by Django 2.1.3 on 2018-12-21 17:07

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('website', '0003_auto_20181205_1942'),
    ]

    operations = [
        migrations.RemoveField(
            model_name='admitcarddetail',
            name='admitCard',
        ),
        migrations.RemoveField(
            model_name='answerkeydetail',
            name='answerKey',
        ),
        migrations.RemoveField(
            model_name='resultdetail',
            name='result',
        ),
        migrations.AlterField(
            model_name='jobdetail',
            name='ltstUp',
            field=models.TextField(default=0),
            preserve_default=False,
        ),
        migrations.AlterField(
            model_name='jobdetail',
            name='totVacc',
            field=models.TextField(),
        ),
        migrations.DeleteModel(
            name='AdmitCardDetail',
        ),
        migrations.DeleteModel(
            name='AdmitCardResult',
        ),
        migrations.DeleteModel(
            name='AnswerKeyDetail',
        ),
        migrations.DeleteModel(
            name='AnswerKeyResult',
        ),
        migrations.DeleteModel(
            name='ResultDetail',
        ),
        migrations.DeleteModel(
            name='ResultResult',
        ),
    ]