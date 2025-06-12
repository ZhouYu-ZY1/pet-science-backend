package com.pet_science.utils;

import com.pet_science.pojo.content.Content;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class HttpUtils {
    public static OkHttpClient client = new OkHttpClient()
            .newBuilder().connectTimeout(50000, TimeUnit.MILLISECONDS)
            .readTimeout(60000, TimeUnit.MILLISECONDS).build();


    //默认Cookie
//    public static final String cookie = "ttwid=1%7Cy7-wgq699S5dfD3cwmlihWBJ_k0Itkr-EhSSy1ZQ5wM%7C1721892345%7C234d8ba0bcc10dea1d6ab0ab7a9bd962dba04ed5175a8984b6c9a9a7e806d891; UIFID_TEMP=3c3e9d4a635845249e00419877a3730e2149197a63ddb1d8525033ea2b3354c2e13a8efc0737ffdb2d89b7f1396ae6c753d3a0e928e7576fb849b056e7a885fcb63e9a3de8ac0b28abd5426aa3f636c7168f4f248c11fae9869f7c8b6082cae453e94e92e37e23457464bc55a8fe8c05; fpk1=U2FsdGVkX19bRxdI0zJSdj879yBkQRM79FHi8KsM4e84X+//P6tK2h00CuPmjvonC846JvHB/+fa8THZFHCPfg==; fpk2=f1f6b29a6cc1f79a0fea05b885aa33d0; SEARCH_RESULT_LIST_TYPE=%22single%22; xgplayer_user_id=573747585579; UIFID=3c3e9d4a635845249e00419877a3730e2149197a63ddb1d8525033ea2b3354c27408bce5219e2fa374624a4a7620947f974234b76297772ac00c7416492af1039e558600e8a3593470cfe202bd0fefe8ac898fd5b454621e5173c2d27d846be44ffa2b919fa8f5e5547f703b6473c9706347ff45f748057592eb17da050ecd7152db10c65a1594346daa429705768d766d348c8f0774538707d499a9c0810d126b829f2987a6bd53c65dd992224cbdbf7aebd8f07738e7560534ebcc40e4a9b5; hevc_supported=true; s_v_web_id=verify_m3cssxot_nGMCjVUb_AS8M_4Uk4_95fx_ilcrcqL9qPOL; passport_csrf_token=05dde154ea4260d0166c150067be65c3; passport_csrf_token_default=05dde154ea4260d0166c150067be65c3; bd_ticket_guard_client_web_domain=2; douyin.com; is_dash_user=1; device_web_cpu_core=16; device_web_memory_size=8; architecture=amd64; dy_swidth=1536; dy_sheight=864; csrf_session_id=7f156f9edc3de0f10fb757bcbb1e6e37; strategyABtestKey=%221733124841.361%22; FORCE_LOGIN=%7B%22videoConsumedRemainSeconds%22%3A180%7D; volume_info=%7B%22isUserMute%22%3Afalse%2C%22isMute%22%3Atrue%2C%22volume%22%3A0.5%7D; download_guide=%223%2F20241202%2F0%22; __ac_signature=_02B4Z6wo00f01yPvWWQAAIDC.LSqKim4skMjz13AAK.Bef; gulu_source_res=eyJwX2luIjoiNGEyZmE1ZTg5YTg1M2ViNDJiOTRmMzNjODI3MThlYzAyMDdmNDc5ZjdhYTgxNmE5ZjlmZmNjNmI3OGFhZWZmNiJ9; sdk_source_info=7e276470716a68645a606960273f276364697660272927676c715a6d6069756077273f276364697660272927666d776a68605a607d71606b766c6a6b5a7666776c7571273f275e58272927666a6b766a69605a696c6061273f27636469766027292762696a6764695a7364776c6467696076273f275e5827292771273f273d343436373d3c373436363234272927676c715a75776a716a666a69273f2763646976602778; bit_env=tfkR43oc1TAbZyNv1ysXe1mIZJJTAPiuBvFM3OcXRxafq-_8LeYuvKJi-Ls-S10sOCW5gbNnWa-hRCnizl5KfWNWuGUTI3pnt7nbyBdoZUeKNpXqpaIfGowhLvO7vintwvBscYd-tPkya4bkPgbZ4m5RPRnLH4d9ttJsbhHFeBuT8lJFTgGKPJUgjfDSrcl8txaFQ7l80Ef1ICSOtkoBiJF4PhEIXu4sS9zIHfcLXsbr34t2ikmEOlSJiuZyn_ZPimfFGORNXHLULx8pkBJsx2dgsQRQ42fsCb3CNtita8xJDnlI9eKq1AB_Lmnb2OHoYsYwWMiNlx7qyFIsjC2YWq9S8_4gXzTBvTRZdIQILf30bdrljnl3jWM6dZTsbW-JbwM0FoHtdmUB0dGI05RHdBA9jh7Ehsn1EzOVmmYNBphHKsjL7oloIVhy-8yC-0ToVhthOtNWPbvj7JNpQPGDKL4myam-ZwSKu_XJvmzn7LiRs5HucuD2Y8aeASy3BH6G; passport_auth_mix_state=l77prm6nsshqvnosgn6phiguctwozx2h; passport_mfa_token=CjUpVNetvpB8Zr1jZ2tbtInUu38uUWEvjROrAsVcmkMpmk9I5M8B0mNz0oEvbuGjuQF1CQtF4BpKCjwtGudUs%2BSswQPE11KonfIvva%2BR7rYcYQCWQbosDQsJqBhxE752x56MMscPL1VmmkDXmaqHZlDWDiOVutQQr4PjDRj2sdFsIAIiAQOcqBrs; d_ticket=ddee2028b8cf6666247e3c9272741fbdf9b5c; passport_assist_user=Cj2R92G3UV6xb-jmf4xxvJn9rGYhyqNoeb5ZSpOx_Q2bucZ4dHqM4hzWGUF-w-LjdYFC6pf-RxnE4DMztLwyGkoKPOe34cqvpZUziLeaq-Xv5U9l4jR_cjtOyCIoRKlgiVWeVaaM8cxcxAt5-eVtr1Oft8zr4nnFdghJhgLFZhDlguMNGImv1lQgASIBA9NkpvA%3D; n_mh=uMdSiFmP8i2dWj6705vGWkn9--naSG-Lx121BtF2X_U; sso_uid_tt=5783a2886eed7c61f2db7d24b6c405de; sso_uid_tt_ss=5783a2886eed7c61f2db7d24b6c405de; toutiao_sso_user=e3c58d87b0bad2ebe435a00058dfdd0c; toutiao_sso_user_ss=e3c58d87b0bad2ebe435a00058dfdd0c; sid_ucp_sso_v1=1.0.0-KGMzNDY2ODNkYzM3N2UyMTk5ZmU2ZjE2MTcyNmJlNTdjNGFhOGY3YWUKHwiqiJL99wIQl-21ugYY7zEgDDCNotLZBTgGQPQHSAYaAmxxIiBlM2M1OGQ4N2IwYmFkMmViZTQzNWEwMDA1OGRmZGQwYw; ssid_ucp_sso_v1=1.0.0-KGMzNDY2ODNkYzM3N2UyMTk5ZmU2ZjE2MTcyNmJlNTdjNGFhOGY3YWUKHwiqiJL99wIQl-21ugYY7zEgDDCNotLZBTgGQPQHSAYaAmxxIiBlM2M1OGQ4N2IwYmFkMmViZTQzNWEwMDA1OGRmZGQwYw; login_time=1733129879643; passport_auth_status=52cb51f862f9f9b8ebaf0776aa492051%2C; passport_auth_status_ss=52cb51f862f9f9b8ebaf0776aa492051%2C; uid_tt=a68f5506ce7d5e57aebf0fa0e442282f; uid_tt_ss=a68f5506ce7d5e57aebf0fa0e442282f; sid_tt=b1a080f6bf22a258d2e4b56893e0be43; sessionid=b1a080f6bf22a258d2e4b56893e0be43; sessionid_ss=b1a080f6bf22a258d2e4b56893e0be43; is_staff_user=false; __ac_nonce=0674d769900d5331733a5; SelfTabRedDotControl=%5B%5D; FOLLOW_LIVE_POINT_INFO=%22MS4wLjABAAAASmPmcSDRbNaw1E1RDCQkwWeYoTNcpq02bL4GM1_9u_Y%2F1733155200000%2F0%2F1733129894052%2F0%22; _bd_ticket_crypt_doamin=2; _bd_ticket_crypt_cookie=74e5bf5e06aeb90df2e2297b72556239; __security_server_data_status=1; publish_badge_show_info=%220%2C0%2C0%2C1733129895728%22; xg_device_score=7.90435294117647; sid_guard=b1a080f6bf22a258d2e4b56893e0be43%7C1733129897%7C5183985%7CFri%2C+31-Jan-2025+08%3A58%3A02+GMT; sid_ucp_v1=1.0.0-KDZmZmY2Yjc3OTE2NzYyMDBhNjkzYjgwOGIwMDg0ZTIwNjA1YTJmNzMKGQiqiJL99wIQqe21ugYY7zEgDDgGQPQHSAQaAmxxIiBiMWEwODBmNmJmMjJhMjU4ZDJlNGI1Njg5M2UwYmU0Mw; ssid_ucp_v1=1.0.0-KDZmZmY2Yjc3OTE2NzYyMDBhNjkzYjgwOGIwMDg0ZTIwNjA1YTJmNzMKGQiqiJL99wIQqe21ugYY7zEgDDgGQPQHSAQaAmxxIiBiMWEwODBmNmJmMjJhMjU4ZDJlNGI1Njg5M2UwYmU0Mw; biz_trace_id=2826297d; store-region=cn-sc; store-region-src=uid; IsDouyinActive=true; stream_recommend_feed_params=%22%7B%5C%22cookie_enabled%5C%22%3Atrue%2C%5C%22screen_width%5C%22%3A1536%2C%5C%22screen_height%5C%22%3A864%2C%5C%22browser_online%5C%22%3Atrue%2C%5C%22cpu_core_num%5C%22%3A16%2C%5C%22device_memory%5C%22%3A8%2C%5C%22downlink%5C%22%3A10%2C%5C%22effective_type%5C%22%3A%5C%224g%5C%22%2C%5C%22round_trip_time%5C%22%3A100%7D%22; bd_ticket_guard_client_data=eyJiZC10aWNrZXQtZ3VhcmQtdmVyc2lvbiI6MiwiYmQtdGlja2V0LWd1YXJkLWl0ZXJhdGlvbi12ZXJzaW9uIjoxLCJiZC10aWNrZXQtZ3VhcmQtcmVlLXB1YmxpYy1rZXkiOiJCS2YzdFFHZ3RBaU9ReGlkcDlKeTVmc3pGSit3T01KWTV2MUk0VXZoMGlHekl3TUovcWhkU0JtUW1UUWxsOWxyWmhyMG1ZZkpjZ2kwQ3Foa2ZRcDVac0E9IiwiYmQtdGlja2V0LWd1YXJkLXdlYi12ZXJzaW9uIjoyfQ%3D%3D; passport_fe_beating_status=true; stream_player_status_params=%22%7B%5C%22is_auto_play%5C%22%3A0%2C%5C%22is_full_screen%5C%22%3A0%2C%5C%22is_full_webscreen%5C%22%3A0%2C%5C%22is_mute%5C%22%3A1%2C%5C%22is_speed%5C%22%3A1%2C%5C%22is_visible%5C%22%3A1%7D%22; home_can_add_dy_2_desktop=%221%22; odin_tt=7ea8d2b5a709e547be1dc6228fbb1f6042a13e8e141089d96afc69ad3a4e50cc04bd21b49ef106f9d9b4a07724aca74c";
//    public static final String cookie = "store-region-src=uid; LOGIN_STATUS=1; store-region=cn-sc; ttwid=1%7Cy7-wgq699S5dfD3cwmlihWBJ_k0Itkr-EhSSy1ZQ5wM%7C1721892345%7C234d8ba0bcc10dea1d6ab0ab7a9bd962dba04ed5175a8984b6c9a9a7e806d891; UIFID_TEMP=3c3e9d4a635845249e00419877a3730e2149197a63ddb1d8525033ea2b3354c2e13a8efc0737ffdb2d89b7f1396ae6c753d3a0e928e7576fb849b056e7a885fcb63e9a3de8ac0b28abd5426aa3f636c7168f4f248c11fae9869f7c8b6082cae453e94e92e37e23457464bc55a8fe8c05; fpk1=U2FsdGVkX19bRxdI0zJSdj879yBkQRM79FHi8KsM4e84X+//P6tK2h00CuPmjvonC846JvHB/+fa8THZFHCPfg==; fpk2=f1f6b29a6cc1f79a0fea05b885aa33d0; SEARCH_RESULT_LIST_TYPE=%22single%22; xgplayer_user_id=573747585579; UIFID=3c3e9d4a635845249e00419877a3730e2149197a63ddb1d8525033ea2b3354c27408bce5219e2fa374624a4a7620947f974234b76297772ac00c7416492af1039e558600e8a3593470cfe202bd0fefe8ac898fd5b454621e5173c2d27d846be44ffa2b919fa8f5e5547f703b6473c9706347ff45f748057592eb17da050ecd7152db10c65a1594346daa429705768d766d348c8f0774538707d499a9c0810d126b829f2987a6bd53c65dd992224cbdbf7aebd8f07738e7560534ebcc40e4a9b5; s_v_web_id=verify_m48ebns7_zzZTJGCZ_bKqC_40ox_8E3i_fSkarKB5HzkZ; douyin.com; is_dash_user=0; device_web_cpu_core=16; device_web_memory_size=8; architecture=amd64; dy_swidth=1536; dy_sheight=864; strategyABtestKey=%221733226531.493%22; csrf_session_id=c4e70271b292048c81eb469209894ba7; passport_csrf_token=0fdee8e6ad158f0e78d43b3ec346c562; passport_csrf_token_default=0fdee8e6ad158f0e78d43b3ec346c562; sso_uid_tt=ae9cee9335ba1351ecc36541ea4967c7; sso_uid_tt_ss=ae9cee9335ba1351ecc36541ea4967c7; toutiao_sso_user=ecb688f3577a5fa20a1ea3bb89104729; toutiao_sso_user_ss=ecb688f3577a5fa20a1ea3bb89104729; sid_ucp_sso_v1=1.0.0-KGY1YWM3ZTViMWQ2OWUyN2M5OTliY2ZjOWUwZWM1NmQwZjNjMGQzYWMKCRCk4Lu6BhjvMRoCbHEiIGVjYjY4OGYzNTc3YTVmYTIwYTFlYTNiYjg5MTA0NzI5; ssid_ucp_sso_v1=1.0.0-KGY1YWM3ZTViMWQ2OWUyN2M5OTliY2ZjOWUwZWM1NmQwZjNjMGQzYWMKCRCk4Lu6BhjvMRoCbHEiIGVjYjY4OGYzNTc3YTVmYTIwYTFlYTNiYjg5MTA0NzI5; volume_info=%7B%22isUserMute%22%3Afalse%2C%22isMute%22%3Afalse%2C%22volume%22%3A0.6%7D; sid_guard=dc2ac57054038f6d77bf6ca57835bb67%7C1733226533%7C21600%7CTue%2C+03-Dec-2024+17%3A48%3A53+GMT; uid_tt=f86623535300373a7775b9c1baaed45a; uid_tt_ss=f86623535300373a7775b9c1baaed45a; sid_tt=dc2ac57054038f6d77bf6ca57835bb67; sessionid=dc2ac57054038f6d77bf6ca57835bb67; sessionid_ss=dc2ac57054038f6d77bf6ca57835bb67; is_staff_user=false; sid_ucp_v1=1.0.0-KDQ0ZTQzZjcxYzc1NzRiMzYzZWVjOWI0NWM0MTBiYTgzNjJjYWFkYTEKCBCl4Lu6BhgNGgJsZiIgZGMyYWM1NzA1NDAzOGY2ZDc3YmY2Y2E1NzgzNWJiNjc; ssid_ucp_v1=1.0.0-KDQ0ZTQzZjcxYzc1NzRiMzYzZWVjOWI0NWM0MTBiYTgzNjJjYWFkYTEKCBCl4Lu6BhgNGgJsZiIgZGMyYWM1NzA1NDAzOGY2ZDc3YmY2Y2E1NzgzNWJiNjc; __ac_nonce=0674ef02500dc448cbea5; __ac_signature=_02B4Z6wo00f01o1BxrAAAIDDUho1.XCCTeqNYcIAAMQqqQTWscdqlfwe66Da6bbbOfE5fYFQeSRXaSMIJOl720NcAouRjz7iIMQwd-POf8NJSD7kHDpRSCZAyRw-zyxGKLMzqufkfjC16wV989; IsDouyinActive=true; home_can_add_dy_2_desktop=%220%22; stream_recommend_feed_params=%22%7B%5C%22cookie_enabled%5C%22%3Atrue%2C%5C%22screen_width%5C%22%3A1536%2C%5C%22screen_height%5C%22%3A864%2C%5C%22browser_online%5C%22%3Atrue%2C%5C%22cpu_core_num%5C%22%3A16%2C%5C%22device_memory%5C%22%3A8%2C%5C%22downlink%5C%22%3A8.65%2C%5C%22effective_type%5C%22%3A%5C%224g%5C%22%2C%5C%22round_trip_time%5C%22%3A250%7D%22; stream_player_status_params=%22%7B%5C%22is_auto_play%5C%22%3A0%2C%5C%22is_full_screen%5C%22%3A0%2C%5C%22is_full_webscreen%5C%22%3A0%2C%5C%22is_mute%5C%22%3A0%2C%5C%22is_speed%5C%22%3A1%2C%5C%22is_visible%5C%22%3A1%7D%22; odin_tt=dc4c8166f7cc942a875f3405bc1f0147d5834bec3e726e8b950624522724ae4b71af14765eda23ce91ee437b49d92f2b842611f1420cba9ef7cdb958570de7ab590758b90c8f7563a89b96d354f1704e";
    // 萌宠视界
    public static final String cookie = "UIFID_TEMP=c4a29131752d59acb78af076c3dbdd52744118e38e80b4b96439ef1e20799db071f7f8cbddfd21a8ec6176d72f2f1b7fd0ce3a1b816b2c1494bc087708f14f7d6dd8f5fcec8fab3357be4108706a22aea466e434d542319e275d87f134eb7d6f1e8c7a7920fd3e8afa391c65fea9ae9a; hevc_supported=true; fpk1=U2FsdGVkX1+2x8qHX56uIWZL0W5+CLy1IL5Hnd3/N8/45fNQN8vlPIdDzuek63BzHAG21JeKZzmcsOy2YoA+rg==; fpk2=f51bb482c660d0eeadd1f058058a2b35; UIFID=c4a29131752d59acb78af076c3dbdd52744118e38e80b4b96439ef1e20799db071f7f8cbddfd21a8ec6176d72f2f1b7fd0ce3a1b816b2c1494bc087708f14f7d0b99b73c1a9bfc3d389163a69ebe17ff6eb3b0c2b3294e0e106a0c0f390f235ccf12272283511fb91f38668deb932f7f85d9dc6ded702aa6b1e14146ebe59952214602fa6a6d1cfed37b7e74b89832fe44cd830004f45f64c91ff7b5ecbe77a17f853f92bf0755bdb14edae61a49d09afae6355904d9bcab48038afc882a8732; bd_ticket_guard_client_web_domain=2; ttwid=1%7CD1LuJVrrT2aYCFpDFEq7Eugp4z8QFDRsLyTiEhxBxgA%7C1743385509%7Cc875ce2a4d6daa849b5f8c989385551a67ae20cbe890056f4f33e620551587bc; s_v_web_id=verify_ma88oeu2_FKDJEsUl_cYMe_4Mfh_BWIw_DLGeoCX7THis; SearchMultiColumnLandingAbVer=2; SEARCH_RESULT_LIST_TYPE=%22multi%22; xgplayer_device_id=78850219106; xgplayer_user_id=968521170888; csrf_session_id=af536807226deb98c556f04910d7edf7; douyin.com; device_web_cpu_core=16; device_web_memory_size=8; architecture=amd64; dy_swidth=1536; dy_sheight=864; is_dash_user=1; __security_mc_1_s_sdk_crypt_sdk=e9aeee74-49df-8b70; __security_mc_1_s_sdk_cert_key=17e62cae-4708-a0db; strategyABtestKey=%221748672185.936%22; FORCE_LOGIN=%7B%22videoConsumedRemainSeconds%22%3A180%2C%22isForcePopClose%22%3A1%7D; passport_csrf_token=a08b223bf03963a46faccd1ec0d68e21; passport_csrf_token_default=a08b223bf03963a46faccd1ec0d68e21; biz_trace_id=794e07f1; passport_mfa_token=CjfiEwjvCD%2F6NfVX2rlYu89DjepsY1T3nEkR4zqCerD5YKuxJ4X0hpK%2FI5DFxYjp%2BTsbTS6jvoU3GkoKPAAAAAAAAAAAAABPD5JV1q0%2B4r6AL%2Bjf6D8zOoZ31Lc2%2B0o64TX3Bu32erqllZy5zQAqszh7DjgP8hXOIRDS6vINGPax0WwgAiIBAw5PFAY%3D; d_ticket=164709948840aaa9f3a3169d969abce05d20c; session_tlb_tag=sttt%7C15%7CyUEsp3OoqZRaKv_t5RrOX__________GE8dc9JzXEW6d4RVQ02vDrrurnf9JFX7jFwSDnuwaLMo%3D; passport_assist_user=CkFWWJLcMTEqIf0bGlCY6qdX6H7Kz9xewNznl1SnoYUXcuosoZQYz2vQXw6zeLitPex5VPF0_OrnHLdxom7fwjBJBhpKCjwAAAAAAAAAAAAATw_FJCDAi4DHYZxSbL3I--d3CFjmmbFxtIOgoEOPI2QGMruOEsY_oarZACsXhCrPVGAQzOvyDRiJr9ZUIAEiAQOULTjz; n_mh=OgrJg11d5cPgc6Gpo0C4Z17d82jtKXU_XMPd1HGB46I; sid_guard=c9412ca773a8a9945a2affede51ace5f%7C1748672240%7C5183999%7CWed%2C+30-Jul-2025+06%3A17%3A19+GMT; uid_tt=583ea3c2f5467947356ea81a2f63a3f2; uid_tt_ss=583ea3c2f5467947356ea81a2f63a3f2; sid_tt=c9412ca773a8a9945a2affede51ace5f; sessionid=c9412ca773a8a9945a2affede51ace5f; sessionid_ss=c9412ca773a8a9945a2affede51ace5f; is_staff_user=false; sid_ucp_v1=1.0.0-KGNlMWZhM2VjZTRkNTQ0ZjI1YjgxNjlmZTUwOGIxMWM3NjI1NjcwNDQKIQi71bDQsM2bBBDwverBBhjvMSAMMIvBwr0GOAdA9AdIBBoCbGYiIGM5NDEyY2E3NzNhOGE5OTQ1YTJhZmZlZGU1MWFjZTVm; ssid_ucp_v1=1.0.0-KGNlMWZhM2VjZTRkNTQ0ZjI1YjgxNjlmZTUwOGIxMWM3NjI1NjcwNDQKIQi71bDQsM2bBBDwverBBhjvMSAMMIvBwr0GOAdA9AdIBBoCbGYiIGM5NDEyY2E3NzNhOGE5OTQ1YTJhZmZlZGU1MWFjZTVm; login_time=1748672239605; __ac_nonce=0683a9ef0000efd98b52c; __ac_signature=_02B4Z6wo00f016eP9PgAAIDCeNQHteB8jt-nr.BAAIHhce; SelfTabRedDotControl=%5B%5D; _bd_ticket_crypt_cookie=f2437254ebd98e518d9052dfcc74f40d; __security_mc_1_s_sdk_sign_data_key_web_protect=dca0cc85-4d9d-8b0d; __security_server_data_status=1; publish_badge_show_info=%220%2C0%2C0%2C1748672248250%22; SelectedFeedCache=3; download_guide=%223%2F20250531%2F0%22; FOLLOW_NUMBER_YELLOW_POINT_INFO=%22MS4wLjABAAAAaMHrEvXwRZPM1H-oF1H7CCtD0eY45U7nlezZy4kivgESWwydSU168WAN_ygV7nYx%2F1748707200000%2F0%2F1748672601351%2F0%22; xg_device_score=7.676183935447684; stream_recommend_feed_params=%22%7B%5C%22cookie_enabled%5C%22%3Atrue%2C%5C%22screen_width%5C%22%3A1536%2C%5C%22screen_height%5C%22%3A864%2C%5C%22browser_online%5C%22%3Atrue%2C%5C%22cpu_core_num%5C%22%3A16%2C%5C%22device_memory%5C%22%3A8%2C%5C%22downlink%5C%22%3A10%2C%5C%22effective_type%5C%22%3A%5C%224g%5C%22%2C%5C%22round_trip_time%5C%22%3A50%7D%22; volume_info=%7B%22isUserMute%22%3Afalse%2C%22isMute%22%3Atrue%2C%22volume%22%3A0.5%7D; FOLLOW_LIVE_POINT_INFO=%22MS4wLjABAAAAaMHrEvXwRZPM1H-oF1H7CCtD0eY45U7nlezZy4kivgESWwydSU168WAN_ygV7nYx%2F1748707200000%2F0%2F0%2F1748673599341%22; passport_fe_beating_status=false; IsDouyinActive=true; bd_ticket_guard_client_data=eyJiZC10aWNrZXQtZ3VhcmQtdmVyc2lvbiI6MiwiYmQtdGlja2V0LWd1YXJkLWl0ZXJhdGlvbi12ZXJzaW9uIjoxLCJiZC10aWNrZXQtZ3VhcmQtcmVlLXB1YmxpYy1rZXkiOiJCS2YzdFFHZ3RBaU9ReGlkcDlKeTVmc3pGSit3T01KWTV2MUk0VXZoMGlHekl3TUovcWhkU0JtUW1UUWxsOWxyWmhyMG1ZZkpjZ2kwQ3Foa2ZRcDVac0E9IiwiYmQtdGlja2V0LWd1YXJkLXdlYi12ZXJzaW9uIjoyfQ%3D%3D; stream_player_status_params=%22%7B%5C%22is_auto_play%5C%22%3A0%2C%5C%22is_full_screen%5C%22%3A0%2C%5C%22is_full_webscreen%5C%22%3A0%2C%5C%22is_mute%5C%22%3A1%2C%5C%22is_speed%5C%22%3A1%2C%5C%22is_visible%5C%22%3A1%7D%22; home_can_add_dy_2_desktop=%221%22; odin_tt=ee77d9e7fb86d01d22994c8ae9b274c80439a3689c3a01825fd13afe441ed04c8c126767ae97a6e94393fe558718a7ad2e737d803a7a2cbd306cfe4e55909f52";

    /**
     * 推荐视频
     */
    public static List<Content> recommendVideo(String cookie){
        if(cookie == null || cookie.isEmpty()){
            cookie = HttpUtils.cookie;
        }
        List<Content> contents = new ArrayList<>();
        Request request = new Request.Builder()
                .url("https://www.douyin.com/aweme/v1/web/tab/feed/?device_platform=webapp&aid=6383&channel=channel_pc_web&tag_id=&share_aweme_id=&live_insert_type=&count=20&refresh_index=2&video_type_select=1&aweme_pc_rec_raw_data=%7B%22is_client%22%3Afalse%2C%22ff_danmaku_status%22%3A1%2C%22danmaku_switch_status%22%3A1%2C%22is_dash_user%22%3A1%2C%22is_auto_play%22%3A0%2C%22is_full_screen%22%3A0%2C%22is_full_webscreen%22%3A0%2C%22is_mute%22%3A1%2C%22is_speed%22%3A1%2C%22is_visible%22%3A1%2C%22related_recommend%22%3A1%7D&globalwid=&pull_type=2&min_window=0&free_right=0&view_count=2&plug_block=0&ug_source=&creative_id=&pc_client_type=1&pc_libra_divert=Windows&version_code=170400&version_name=17.4.0&cookie_enabled=true&screen_width=1536&screen_height=864&browser_language=zh-CN&browser_platform=Win32&browser_name=Chrome&browser_version=131.0.0.0&browser_online=true&engine_name=Blink&engine_version=131.0.0.0&os_name=Windows&os_version=10&cpu_core_num=16&device_memory=8&platform=PC&downlink=10&effective_type=4g&round_trip_time=100&webid=7395471247238809125&msToken=Y2QCF10ovIp9-tioxtXn2k6Ll03msLIWhi8URGJVT9aXTCN8ej4Nm0KcV3aejH-a6nmNnTBjmvFfwkmDSJ2vvZFvgl3H8rgRbbKtVxLlhguf-uKLtesuEfC-VwseigJC_BpYWPr-6NGfv5k7vgxGr57OTaX_GgpEGfazjXh92Smmngh2gsMp&a_bogus=m64fge7JENRbcVFtmKbvCA3l82LlrsSyjNToSwPTyNKhbHMay8N0iPeGJoq9mayqaWpshe37GdFlbEVcBGUkZorkKmkfSmJyU4%2FA98soZqwXGUG%2FEN6YSg4zowsF0miN-59rEAD5XsMN2nxRVqVBlBBa95zo5cEgbrpAp2S9GDC8psLTV9QXeVfWk1E%3D&verifyFp=verify_m3cssxot_nGMCjVUb_AS8M_4Uk4_95fx_ilcrcqL9qPOL&fp=verify_m3cssxot_nGMCjVUb_AS8M_4Uk4_95fx_ilcrcqL9qPOL")
                .addHeader("user-agent",randomUA("windows"))
                .addHeader("accept","application/json, text/plain, */*")
                .addHeader("accept-language","zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6")
                .addHeader("sec-ch-ua","\"Not A(Brand\";v=\"99\", \"Microsoft Edge\";v=\"121\", \"Chromium\";v=\"121\"")
                .addHeader("priority","u=1, i")
                .addHeader("sec-ch-ua-mobile","?0")
                .addHeader("sec-ch-ua-platform","\"Windows\"")
                .addHeader("sec-fetch-dest","empty")
                .addHeader("sec-fetch-mode","cors")
                .addHeader("sec-fetch-site","same-origin")
//                .addHeader("cookie","ttwid=1%7Cy7-wgq699S5dfD3cwmlihWBJ_k0Itkr-EhSSy1ZQ5wM%7C1721892345%7C234d8ba0bcc10dea1d6ab0ab7a9bd962dba04ed5175a8984b6c9a9a7e806d891; UIFID_TEMP=3c3e9d4a635845249e00419877a3730e2149197a63ddb1d8525033ea2b3354c2e13a8efc0737ffdb2d89b7f1396ae6c753d3a0e928e7576fb849b056e7a885fcb63e9a3de8ac0b28abd5426aa3f636c7168f4f248c11fae9869f7c8b6082cae453e94e92e37e23457464bc55a8fe8c05; fpk1=U2FsdGVkX19bRxdI0zJSdj879yBkQRM79FHi8KsM4e84X+//P6tK2h00CuPmjvonC846JvHB/+fa8THZFHCPfg==; fpk2=f1f6b29a6cc1f79a0fea05b885aa33d0; SEARCH_RESULT_LIST_TYPE=%22single%22; xgplayer_user_id=573747585579; UIFID=3c3e9d4a635845249e00419877a3730e2149197a63ddb1d8525033ea2b3354c27408bce5219e2fa374624a4a7620947f974234b76297772ac00c7416492af1039e558600e8a3593470cfe202bd0fefe8ac898fd5b454621e5173c2d27d846be44ffa2b919fa8f5e5547f703b6473c9706347ff45f748057592eb17da050ecd7152db10c65a1594346daa429705768d766d348c8f0774538707d499a9c0810d126b829f2987a6bd53c65dd992224cbdbf7aebd8f07738e7560534ebcc40e4a9b5; hevc_supported=true; s_v_web_id=verify_m3cssxot_nGMCjVUb_AS8M_4Uk4_95fx_ilcrcqL9qPOL; passport_csrf_token=05dde154ea4260d0166c150067be65c3; passport_csrf_token_default=05dde154ea4260d0166c150067be65c3; bd_ticket_guard_client_web_domain=2; douyin.com; is_dash_user=1; device_web_cpu_core=16; device_web_memory_size=8; architecture=amd64; dy_swidth=1536; dy_sheight=864; csrf_session_id=7f156f9edc3de0f10fb757bcbb1e6e37; strategyABtestKey=%221733124841.361%22; FORCE_LOGIN=%7B%22videoConsumedRemainSeconds%22%3A180%7D; volume_info=%7B%22isUserMute%22%3Afalse%2C%22isMute%22%3Atrue%2C%22volume%22%3A0.5%7D; download_guide=%223%2F20241202%2F0%22; __ac_signature=_02B4Z6wo00f01yPvWWQAAIDC.LSqKim4skMjz13AAK.Bef; gulu_source_res=eyJwX2luIjoiNGEyZmE1ZTg5YTg1M2ViNDJiOTRmMzNjODI3MThlYzAyMDdmNDc5ZjdhYTgxNmE5ZjlmZmNjNmI3OGFhZWZmNiJ9; sdk_source_info=7e276470716a68645a606960273f276364697660272927676c715a6d6069756077273f276364697660272927666d776a68605a607d71606b766c6a6b5a7666776c7571273f275e58272927666a6b766a69605a696c6061273f27636469766027292762696a6764695a7364776c6467696076273f275e5827292771273f273d343436373d3c373436363234272927676c715a75776a716a666a69273f2763646976602778; bit_env=tfkR43oc1TAbZyNv1ysXe1mIZJJTAPiuBvFM3OcXRxafq-_8LeYuvKJi-Ls-S10sOCW5gbNnWa-hRCnizl5KfWNWuGUTI3pnt7nbyBdoZUeKNpXqpaIfGowhLvO7vintwvBscYd-tPkya4bkPgbZ4m5RPRnLH4d9ttJsbhHFeBuT8lJFTgGKPJUgjfDSrcl8txaFQ7l80Ef1ICSOtkoBiJF4PhEIXu4sS9zIHfcLXsbr34t2ikmEOlSJiuZyn_ZPimfFGORNXHLULx8pkBJsx2dgsQRQ42fsCb3CNtita8xJDnlI9eKq1AB_Lmnb2OHoYsYwWMiNlx7qyFIsjC2YWq9S8_4gXzTBvTRZdIQILf30bdrljnl3jWM6dZTsbW-JbwM0FoHtdmUB0dGI05RHdBA9jh7Ehsn1EzOVmmYNBphHKsjL7oloIVhy-8yC-0ToVhthOtNWPbvj7JNpQPGDKL4myam-ZwSKu_XJvmzn7LiRs5HucuD2Y8aeASy3BH6G; passport_auth_mix_state=l77prm6nsshqvnosgn6phiguctwozx2h; passport_mfa_token=CjUpVNetvpB8Zr1jZ2tbtInUu38uUWEvjROrAsVcmkMpmk9I5M8B0mNz0oEvbuGjuQF1CQtF4BpKCjwtGudUs%2BSswQPE11KonfIvva%2BR7rYcYQCWQbosDQsJqBhxE752x56MMscPL1VmmkDXmaqHZlDWDiOVutQQr4PjDRj2sdFsIAIiAQOcqBrs; d_ticket=ddee2028b8cf6666247e3c9272741fbdf9b5c; passport_assist_user=Cj2R92G3UV6xb-jmf4xxvJn9rGYhyqNoeb5ZSpOx_Q2bucZ4dHqM4hzWGUF-w-LjdYFC6pf-RxnE4DMztLwyGkoKPOe34cqvpZUziLeaq-Xv5U9l4jR_cjtOyCIoRKlgiVWeVaaM8cxcxAt5-eVtr1Oft8zr4nnFdghJhgLFZhDlguMNGImv1lQgASIBA9NkpvA%3D; n_mh=uMdSiFmP8i2dWj6705vGWkn9--naSG-Lx121BtF2X_U; sso_uid_tt=5783a2886eed7c61f2db7d24b6c405de; sso_uid_tt_ss=5783a2886eed7c61f2db7d24b6c405de; toutiao_sso_user=e3c58d87b0bad2ebe435a00058dfdd0c; toutiao_sso_user_ss=e3c58d87b0bad2ebe435a00058dfdd0c; sid_ucp_sso_v1=1.0.0-KGMzNDY2ODNkYzM3N2UyMTk5ZmU2ZjE2MTcyNmJlNTdjNGFhOGY3YWUKHwiqiJL99wIQl-21ugYY7zEgDDCNotLZBTgGQPQHSAYaAmxxIiBlM2M1OGQ4N2IwYmFkMmViZTQzNWEwMDA1OGRmZGQwYw; ssid_ucp_sso_v1=1.0.0-KGMzNDY2ODNkYzM3N2UyMTk5ZmU2ZjE2MTcyNmJlNTdjNGFhOGY3YWUKHwiqiJL99wIQl-21ugYY7zEgDDCNotLZBTgGQPQHSAYaAmxxIiBlM2M1OGQ4N2IwYmFkMmViZTQzNWEwMDA1OGRmZGQwYw; login_time=1733129879643; passport_auth_status=52cb51f862f9f9b8ebaf0776aa492051%2C; passport_auth_status_ss=52cb51f862f9f9b8ebaf0776aa492051%2C; uid_tt=a68f5506ce7d5e57aebf0fa0e442282f; uid_tt_ss=a68f5506ce7d5e57aebf0fa0e442282f; sid_tt=b1a080f6bf22a258d2e4b56893e0be43; sessionid=b1a080f6bf22a258d2e4b56893e0be43; sessionid_ss=b1a080f6bf22a258d2e4b56893e0be43; is_staff_user=false; __ac_nonce=0674d769900d5331733a5; SelfTabRedDotControl=%5B%5D; FOLLOW_LIVE_POINT_INFO=%22MS4wLjABAAAASmPmcSDRbNaw1E1RDCQkwWeYoTNcpq02bL4GM1_9u_Y%2F1733155200000%2F0%2F1733129894052%2F0%22; _bd_ticket_crypt_doamin=2; _bd_ticket_crypt_cookie=74e5bf5e06aeb90df2e2297b72556239; __security_server_data_status=1; publish_badge_show_info=%220%2C0%2C0%2C1733129895728%22; xg_device_score=7.90435294117647; sid_guard=b1a080f6bf22a258d2e4b56893e0be43%7C1733129897%7C5183985%7CFri%2C+31-Jan-2025+08%3A58%3A02+GMT; sid_ucp_v1=1.0.0-KDZmZmY2Yjc3OTE2NzYyMDBhNjkzYjgwOGIwMDg0ZTIwNjA1YTJmNzMKGQiqiJL99wIQqe21ugYY7zEgDDgGQPQHSAQaAmxxIiBiMWEwODBmNmJmMjJhMjU4ZDJlNGI1Njg5M2UwYmU0Mw; ssid_ucp_v1=1.0.0-KDZmZmY2Yjc3OTE2NzYyMDBhNjkzYjgwOGIwMDg0ZTIwNjA1YTJmNzMKGQiqiJL99wIQqe21ugYY7zEgDDgGQPQHSAQaAmxxIiBiMWEwODBmNmJmMjJhMjU4ZDJlNGI1Njg5M2UwYmU0Mw; biz_trace_id=2826297d; store-region=cn-sc; store-region-src=uid; IsDouyinActive=true; stream_recommend_feed_params=%22%7B%5C%22cookie_enabled%5C%22%3Atrue%2C%5C%22screen_width%5C%22%3A1536%2C%5C%22screen_height%5C%22%3A864%2C%5C%22browser_online%5C%22%3Atrue%2C%5C%22cpu_core_num%5C%22%3A16%2C%5C%22device_memory%5C%22%3A8%2C%5C%22downlink%5C%22%3A10%2C%5C%22effective_type%5C%22%3A%5C%224g%5C%22%2C%5C%22round_trip_time%5C%22%3A100%7D%22; bd_ticket_guard_client_data=eyJiZC10aWNrZXQtZ3VhcmQtdmVyc2lvbiI6MiwiYmQtdGlja2V0LWd1YXJkLWl0ZXJhdGlvbi12ZXJzaW9uIjoxLCJiZC10aWNrZXQtZ3VhcmQtcmVlLXB1YmxpYy1rZXkiOiJCS2YzdFFHZ3RBaU9ReGlkcDlKeTVmc3pGSit3T01KWTV2MUk0VXZoMGlHekl3TUovcWhkU0JtUW1UUWxsOWxyWmhyMG1ZZkpjZ2kwQ3Foa2ZRcDVac0E9IiwiYmQtdGlja2V0LWd1YXJkLXdlYi12ZXJzaW9uIjoyfQ%3D%3D; passport_fe_beating_status=true; stream_player_status_params=%22%7B%5C%22is_auto_play%5C%22%3A0%2C%5C%22is_full_screen%5C%22%3A0%2C%5C%22is_full_webscreen%5C%22%3A0%2C%5C%22is_mute%5C%22%3A1%2C%5C%22is_speed%5C%22%3A1%2C%5C%22is_visible%5C%22%3A1%7D%22; home_can_add_dy_2_desktop=%221%22; odin_tt=7ea8d2b5a709e547be1dc6228fbb1f6042a13e8e141089d96afc69ad3a4e50cc04bd21b49ef106f9d9b4a07724aca74c")
                .addHeader("cookie",cookie)
                .addHeader("referer","https://www.douyin.com/")
                .build();
        try {
            Response response = client.newCall(request).execute();//发送请求
            String result = Objects.requireNonNull(response.body()).string();
            JSONObject jsonObject = new JSONObject(result);
            JSONArray awemeList = jsonObject.getJSONArray("aweme_list");
            for (int i = 0; i < awemeList.length(); i++) {
                try {
                    JSONObject item = awemeList.getJSONObject(i);
                    Content content = null;
                    switch (item.getInt("aweme_type")){
                        case 68-> content = getImageContent(item);
                        case 0 -> content = getVideoContent(item);
                    }
                    if(content != null){
                        contents.add(content);
                    }

                }catch (Exception e){
                    throw new RuntimeException(e);
                }
            }
        }  catch (Exception e) {
            throw new RuntimeException(e);
        }
        return contents; // 返回视频列表
    }

    private static Content getImageContent(JSONObject item) {
        if(!item.isNull("images")){
            Content content = new Content();
            JSONObject statistics = item.getJSONObject("statistics");
            Integer commentCount = statistics.getInt("comment_count");
            Integer diggCount = statistics.getInt("digg_count");
            Integer shareCount = statistics.getInt("share_count");
            JSONArray images = item.getJSONArray("images");
            StringBuilder imageUrls = new StringBuilder();
            for (int i = 0; i < images.length(); i++) {
                try {
                    JSONObject image = images.getJSONObject(i);
                    String imageUrl = image.getJSONArray("url_list").getString(0);
                    if(i == images.length() - 1){
                        break;
                    }
                    imageUrls.append(imageUrl).append(";");
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
            String musicUrl = "";
            try {
                musicUrl = item.getJSONObject("music").getJSONObject("play_url").getString("uri");
            }catch (Exception e){
                e.printStackTrace();
            }

            JSONObject author = item.getJSONObject("author");
            JSONObject avatarThumb = author.getJSONObject("avatar_thumb");
            JSONArray authorCovers = avatarThumb.getJSONArray("url_list");
            String videoId = item.getString("aweme_id"); //视频ID
            String desc = item.getString("desc"); //视频介绍
            String shareUrl = item.getString("share_url"); //分享地址
            String authorAvatar = authorCovers.getString(0); //作者头像
            String uid = author.getString("uid"); //作者ID
            String nickname = author.getString("nickname"); //作者昵称
            content.setCommentCount(commentCount);
            content.setDiggCount(diggCount);
            content.setShareCount(shareCount);
            content.setType(68); //抖音图文
            content.setLike(false); //默认推荐视频都没有收藏
            content.setVideoSrc(musicUrl);
            content.setCoverSrc(imageUrls.toString());
            content.setVideoId(videoId);
            content.setDesc(desc);
            content.setShareUrl(shareUrl);
            content.setAuthorAvatar(authorAvatar);
            content.setUid(uid);
            content.setNickname(nickname);
            return content;
        }
        return null;
    }

    private static Content getVideoContent(JSONObject item) {
        if(!item.isNull("video")){
            Content content = new Content();
            JSONObject statistics = item.getJSONObject("statistics");
            Integer commentCount = statistics.getInt("comment_count");
            Integer diggCount = statistics.getInt("digg_count");
            Integer shareCount = statistics.getInt("share_count");
            JSONObject videojson = item.getJSONObject("video");
            JSONObject playAddr = videojson.getJSONObject("play_addr");
            JSONArray urlList = playAddr.getJSONArray("url_list");
            JSONObject cover = videojson.getJSONObject("origin_cover");
            JSONArray coverList = cover.getJSONArray("url_list");
            JSONObject author = item.getJSONObject("author");
            JSONObject avatarThumb = author.getJSONObject("avatar_thumb");
            JSONArray authorCovers = avatarThumb.getJSONArray("url_list");

            String videoSrc = urlList.getString(2); //播放地址
            String videoId = item.getString("aweme_id"); //视频ID
            String desc = item.getString("desc"); //视频介绍
            String shareUrl = item.getString("share_url"); //分享地址
            String coverSrc = coverList.getString(0); //图片地址
            String authorAvatar = authorCovers.getString(0); //作者头像
            String uid = author.getString("uid"); //作者ID
            String nickname = author.getString("nickname"); //作者昵称

//                        String videoUrl = getVideoUrl(videoSrc);
//                        if(!videoUrl.isEmpty()){
//                            videoSrc = videoUrl;
//                        }
            content.setCommentCount(commentCount);
            content.setDiggCount(diggCount);
            content.setShareCount(shareCount);

            content.setType(0); //抖音视频
            content.setLike(false); //默认推荐视频都没有收藏
            content.setVideoSrc(videoSrc);
            content.setVideoId(videoId);
            content.setDesc(desc);
            content.setShareUrl(shareUrl);
            content.setCoverSrc(coverSrc);
            content.setAuthorAvatar(authorAvatar);
            content.setUid(uid);
            content.setNickname(nickname);
            return content;
        }
        return null;
    }


    private static String getVideoUrl(String url){
        try {
            // 创建 WebClient 实例
            WebClient webClient = WebClient.builder()
                    .baseUrl(url)
                    .defaultHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                    .defaultHeader("accept-encoding", "gzip, deflate, br, zstd")
                    .defaultHeader("accept-language", "zh-CN,zh;q=0.9")
                    .defaultHeader("priority", "u=0, i")
                    .defaultHeader("sec-ch-ua", "\"Google Chrome\";v=\"131\", \"Chromium\";v=\"131\", \"Not_A Brand\";v=\"24\"")
                    .defaultHeader("sec-ch-ua-mobile", "?0")
                    .defaultHeader("sec-ch-ua-platform", "\"Windows\"")
                    .defaultHeader("sec-fetch-dest", "document")
                    .defaultHeader("sec-fetch-mode", "navigate")
                    .defaultHeader("sec-fetch-site", "none")
                    .defaultHeader("sec-fetch-user", "?1")
                    .defaultHeader("upgrade-insecure-requests", "1")
                    .defaultHeader("user-agent", HttpUtils.randomUA("windows"))
                    .defaultHeader("cookie", "s_v_web_id=verify_m49fh1z8_h9qgl1UQ_eDx4_4Kzx_9T3Q_VdjBm76IkXvF; __ac_nonce=0674fe3dd000f94e42b01; __ac_signature=_02B4Z6wo00f01ycsmQgAAIDC-HdqRY.Gbo8nDJ2AAK6Pd2; ttwid=1%7CY-ASPIdXQIYDERhO0F6DTdl9zf7NXCcP9NzUc3PJxeQ%7C1733288926%7Cf42adccb848c722f27814af3b06aeb009c5bbf0ce7b1c6a0820d315356762bdc; UIFID_TEMP=c4a29131752d59acb78af076c3dbdd52744118e38e80b4b96439ef1e20799db0821d02176388b15e3f30a26ce1d42517ef9fdc485c835950c69508f5e6d656147a54c77d5fb48e4f6a79d639416b6be042ea229d0cc92ea694fa96aa94e13e4e7fd94ee431fc24390f0780b18dd0e708; hevc_supported=true; my_rd=2; dy_swidth=1536; dy_sheight=864; stream_recommend_feed_params=%22%7B%5C%22cookie_enabled%5C%22%3Atrue%2C%5C%22screen_width%5C%22%3A1536%2C%5C%22screen_height%5C%22%3A864%2C%5C%22browser_online%5C%22%3Atrue%2C%5C%22cpu_core_num%5C%22%3A16%2C%5C%22device_memory%5C%22%3A8%2C%5C%22downlink%5C%22%3A10%2C%5C%22effective_type%5C%22%3A%5C%224g%5C%22%2C%5C%22round_trip_time%5C%22%3A200%7D%22; csrf_session_id=b195702ee020434f8829f06b201fb538; fpk1=U2FsdGVkX19l6kDnPJVUS280RegB2iMj2yfOR258OJLBAgLPyGecAlNJXgoM99c/6mAXpB8uEh8p4Ka+lq5DHw==; fpk2=f51bb482c660d0eeadd1f058058a2b35; bd_ticket_guard_client_web_domain=2; UIFID=c4a29131752d59acb78af076c3dbdd52744118e38e80b4b96439ef1e20799db0821d02176388b15e3f30a26ce1d42517ef9fdc485c835950c69508f5e6d656147c919eea61b3446ffa459e7d9ab032ebff559114aad96a4a39ae295307670e17a402777089aedf7bf4291cf24ae9bc282d744fc55574e9d784c0f809d39b0ef155103b96cc599caf676c4cc6e3cc2b9dac2e1aef3ca62f97dec56ed9bc678704e062d58cf94b2507c89d743fc48c57069661e769ea15f6e3aa7e48afddf46c39; douyin.com; xg_device_score=7.631555165619236; device_web_cpu_core=16; device_web_memory_size=8; architecture=amd64; is_dash_user=1; home_can_add_dy_2_desktop=%221%22; strategyABtestKey=%221733288931.907%22; FORCE_LOGIN=%7B%22videoConsumedRemainSeconds%22%3A180%7D; volume_info=%7B%22isUserMute%22%3Afalse%2C%22isMute%22%3Atrue%2C%22volume%22%3A0.5%7D; xgplayer_user_id=942378509361; passport_csrf_token=8f828bcce63db47eb8bbbf4df9e2e47c; passport_csrf_token_default=8f828bcce63db47eb8bbbf4df9e2e47c; bd_ticket_guard_client_data=eyJiZC10aWNrZXQtZ3VhcmQtdmVyc2lvbiI6MiwiYmQtdGlja2V0LWd1YXJkLWl0ZXJhdGlvbi12ZXJzaW9uIjoxLCJiZC10aWNrZXQtZ3VhcmQtcmVlLXB1YmxpYy1rZXkiOiJCS2YzdFFHZ3RBaU9ReGlkcDlKeTVmc3pGSit3T01KWTV2MUk0VXZoMGlHekl3TUovcWhkU0JtUW1UUWxsOWxyWmhyMG1ZZkpjZ2kwQ3Foa2ZRcDVac0E9IiwiYmQtdGlja2V0LWd1YXJkLXdlYi12ZXJzaW9uIjoyfQ%3D%3D; biz_trace_id=1728be45; sdk_source_info=7e276470716a68645a606960273f276364697660272927676c715a6d6069756077273f276364697660272927666d776a68605a607d71606b766c6a6b5a7666776c7571273f275e58272927666a6b766a69605a696c6061273f27636469766027292762696a6764695a7364776c6467696076273f275e5827292771273f27333c3636363c3d3d37363632342778; bit_env=Mc7WSP6dFv-37P397EFqkHShuzkZbgpL0PrUjYJjCupJKSCVIAXgAVurpxSmZ3qdwdhkhIrrCacmhl9RrWIHpZrRI115bzcxopxEcNpYLDFJFf3jAqsn-9PZ1GDBbu7nsWhjyy_Jr66YvcvzNEG_Hyy7EqMRxZwqzsStm5t5MYGGsh2cybIDgdZ4XS2SepMTu5IaK58Fc9-C8bJywtw1ws6HWAImAqNxNZ7odTKJ-5T7Svz6jKadQyx30LG7VNH4K8IbhblEWDB8uWFA2LepGrAedp9aH-xJkMdn5PUFRFsv9go-b2rejB50BlbuAqEM0PEuX_2vGwD2mDJqjfNqFufKqDidiMIcY17rmGq0EXpoW5icAGFFLcAGqMd_gecaYTUnXlt_bnQTcn7qvriFoVNCFhvq524Jstg0zl8iK7VPLR-t__KtyRjhrNV7dPfd323LZCfV6dzGc--niLzq9DYOFX7kdJ2ZmX-57ZrOHqKRny7r5eOVAwHC-bgBwcHS; gulu_source_res=eyJwX2luIjoiNGEyZmE1ZTg5YTg1M2ViNDJiOTRmMzNjODI3MThlYzAyMDdmNDc5ZjdhYTgxNmE5ZjlmZmNjNmI3OGFhZWZmNiJ9; passport_auth_mix_state=mn5u733nmjyxq37dfo6utcz15kfv3f9odb7bs4yinwb7ss1u; IsDouyinActive=false; stream_player_status_params=%22%7B%5C%22is_auto_play%5C%22%3A0%2C%5C%22is_full_screen%5C%22%3A0%2C%5C%22is_full_webscreen%5C%22%3A0%2C%5C%22is_mute%5C%22%3A1%2C%5C%22is_speed%5C%22%3A1%2C%5C%22is_visible%5C%22%3A0%7D%22")
                    .build();

            Mono<String> responseMono = webClient.get()
                    .uri(url)
                    .retrieve()
                    .toEntity(String.class)
                    .flatMap(responseEntity -> {
                        // 检查响应头中的 Location 字段
                        HttpHeaders headers = responseEntity.getHeaders();
                        String finalUrl = headers.getLocation() != null ? headers.getLocation().toString() : "No redirect";
//                        System.out.println("最终地址：" + finalUrl);
                        return Mono.just(finalUrl);
                    });

            // 阻塞调用以获取结果（仅用于演示）
            return responseMono.block();
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
    public static String randomUA(String type){
        Random random = new Random();
        int r1 = random.nextInt(9) + 1;
        int r2 = random.nextInt(9) + 1;
        int r3 = random.nextInt(9) + 1;
        int r4 = random.nextInt(9) + 1;
        if(type.equals("android")){
            return "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/"+r1+"5."+r4+".4"+r2+"32.2"+r3+"2 Mobile Safari/5"+r4+"5.06";
        }else if(type.equals("win2")){
            return "Mozilla/5.0 (Windows; U; Windows NT 5.2;. en-US) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/"+r1+"5."+r4+".2"+r2+"32.2"+r3+"2 Mobile Safari/5"+r4+"3.06";
        }else {
            return "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/"+r1+"5."+r4+".2"+r2+"32.2"+r3+"2 Mobile Safari/5"+r4+"3.06";
        }
    }
}
