package net.ttk1.serverbackup;

import com.amazonaws.services.s3.AmazonS3;
import org.bukkit.configuration.file.FileConfiguration;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BackupTaskRunnerTest {
    @Test
    public void getS3ServiceTest() throws NoSuchFieldException, IllegalAccessException {
        FileConfiguration config = mock(FileConfiguration.class);
        when(config.getBoolean("use_s3", false)).thenReturn(true);
        when(config.getString("s3.region", "ap-northeast-1")).thenReturn("my_region");
        when(config.getBoolean("s3.overwrite", true)).thenReturn(false);
        when(config.getString("s3.bucket_name")).thenReturn("my_bucket");
        when(config.getString("s3.prefix")).thenReturn("my_prefix");
        when(config.getString("s3.access_key")).thenReturn("my_access_key");
        when(config.getString("s3.access_token")).thenReturn("my_access_token");

        S3Service s3Service = BackupTaskRunner.getS3Service(config);
        assertThat(s3Service, notNullValue());

        // access_key/token のチェックまではやらない. region だけチェックする.
        Field s3ClientField = S3Service.class.getDeclaredField("s3Client");
        s3ClientField.setAccessible(true);
        AmazonS3 s3Client = (AmazonS3) s3ClientField.get(s3Service);
        assertThat(s3Client.getRegionName(), is("my_region"));

        // bucket_name のチェック
        Field bucketNameField = S3Service.class.getDeclaredField("bucket_name");
        bucketNameField.setAccessible(true);
        String bucket_name = (String) bucketNameField.get(s3Service);
        assertThat(bucket_name, is("my_bucket"));

        // prefix のチェック
        Field prefixField = S3Service.class.getDeclaredField("prefix");
        prefixField.setAccessible(true);
        String prefix = (String) prefixField.get(s3Service);
        assertThat(prefix, is("my_prefix"));

        // overwrite のチェック
        S3Service.class.getDeclaredField("overwrite").setAccessible(true);
        Field overwriteField = S3Service.class.getDeclaredField("overwrite");
        overwriteField.setAccessible(true);
        boolean overwrite = (boolean) overwriteField.get(s3Service);
        assertThat(overwrite, is(false));
    }

    @Test
    public void getS3ServiceUseS3FalseTest() {
        FileConfiguration config = mock(FileConfiguration.class);
        when(config.getBoolean("use_s3", false)).thenReturn(false);
        S3Service s3Service = BackupTaskRunner.getS3Service(config);
        assertThat(s3Service, nullValue());
    }
}
