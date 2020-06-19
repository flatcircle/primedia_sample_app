package com.primedia.primedia_sample_app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.primedia.primedia_sample_app.App
import com.primedia.primedia_sample_app.models.StreamItemDataModel
import com.primedia.primedia_sample_app.models.StreamItemDataModelType
import com.primedia.primedia_sample_app.triton.Player
import org.kodein.di.generic.instance

class HomeViewModel(context: Application): AndroidViewModel(context) {

    private val player: Player by App.kodein.instance<Player>()

    var currentPosition: Int = 0

    val songList = listOf<StreamItemDataModel>(
        StreamItemDataModel( // not working
            type = StreamItemDataModelType.CLIP,
            image_url = "https://cdn.primedia.co.za/primedia-broadcasting/image/fetch/h_240,w_240,c_fill,g_face/https://www.omnycontent.com/d/clips/5dcefa8e-00a9-4595-8ce1-a4ab0080f142/96be7e09-e535-474b-9d37-a73100ae1f48/4c6e0f1a-37c3-4138-977a-ab8a008f8a10/image.jpg?size=Medium ",
            title = "Is it safe to donate blood during the coronavirus outbreak? ",
            description = "Tracey Lange chats to Michelle Vermeulen - PR, Planning and Promotions Manager at the Western Cape Blood Service.",
            media = "https://traffic.omny.fm/d/clips/5dcefa8e-00a9-4595-8ce1-a4ab0080f142/96be7e09-e535-474b-9d37-a73100ae1f48/4c6e0f1a-37c3-4138-977a-ab8a008f8a10/audio.mp3"
        ),
        StreamItemDataModel(
            type = StreamItemDataModelType.CLIP,
            image_url = " https://cdn.primedia.co.za/primedia-broadcasting/image/fetch/h_240,w_240,c_fill,g_face/https://www.omnycontent.com/d/clips/5dcefa8e-00a9-4595-8ce1-a4ab0080f142/366c625b-e6db-4376-8e27-a6f300c8de6f/ef22e235-537c-4ad5-a480-ab8a008da87e/image.jpg?t=1585211773&size=Medium ",
            title = "The SA Reserve Bank takes extraordinary measures to save the economy ",
            description = "Financial Mail journalist, Warren Thompson, explains the measures announced by the Reserve Bank to mitigate against complete financial disaster amidst",
            media = "https://traffic.omny.fm/d/clips/5dcefa8e-00a9-4595-8ce1-a4ab0080f142/366c625b-e6db-4376-8e27-a6f300c8de6f/ef22e235-537c-4ad5-a480-ab8a008da87e/audio.mp3"
        ),
        StreamItemDataModel(
            type = StreamItemDataModelType.CLIP,
            image_url = "https://cdn.primedia.co.za/primedia-broadcasting/image/fetch/h_240,w_240,c_fill,g_face/https://www.omnycontent.com/d/clips/5dcefa8e-00a9-4595-8ce1-a4ab0080f142/a07bb67a-4bc5-436e-8be2-a6cf00811fcf/c37713ad-29c7-4b52-abf9-ab8a0091653d/image.jpg?size=Medium ",
            title = "A Cape Town based small business changes production lines to manufacture face mask",
            description = "",
            media = "https://traffic.omny.fm/d/clips/5dcefa8e-00a9-4595-8ce1-a4ab0080f142/a07bb67a-4bc5-436e-8be2-a6cf00811fcf/c37713ad-29c7-4b52-abf9-ab8a0091653d/audio.mp3"
        ),
        StreamItemDataModel( // not working
            type = StreamItemDataModelType.CLIP,
            image_url = "https://cdn.primedia.co.za/primedia-broadcasting/image/fetch/h_240,w_240,c_fill,g_face/https://www.omnycontent.com/d/clips/5dcefa8e-00a9-4595-8ce1-a4ab0080f142/366c625b-e6db-4376-8e27-a6f300c8de6f/1771cd0d-2298-41ec-b3ca-ab8a0085cb2d/image.jpg?t=1585210083&size=Medium",
            title = "No walking, no jogging, just stay at home",
            description = "General Bheki Cele assures the public of the role SAPS will play during the national lockdown including their collaboration with the SANDF. ",
            media = "https://traffic.omny.fm/d/clips/5dcefa8e-00a9-4595-8ce1-a4ab0080f142/366c625b-e6db-4376-8e27-a6f300c8de6f/1771cd0d-2298-41ec-b3ca-ab8a0085cb2d/audio.mp3"
        ),
        StreamItemDataModel(
            type = StreamItemDataModelType.CLIP,
            image_url = "https://cdn.primedia.co.za/primedia-broadcasting/image/fetch/h_240,w_240,c_fill,g_face/https://www.omnycontent.com/d/clips/5dcefa8e-00a9-4595-8ce1-a4ab0080f142/a07bb67a-4bc5-436e-8be2-a6cf00811fcf/9df94c92-104a-45f4-9bd5-ab8a00866189/image.jpg?t=1585211218&size=Medium",
            title = "Alan Winde gives details on all plans to deal with 21 day lock down in WC",
            description = "Kieno speaks to Alan Winde Premier at Western Cape Government",
            media = "https://traffic.omny.fm/d/clips/5dcefa8e-00a9-4595-8ce1-a4ab0080f142/a07bb67a-4bc5-436e-8be2-a6cf00811fcf/9df94c92-104a-45f4-9bd5-ab8a00866189/audio.mp3"
        ),
        StreamItemDataModel(
            type = StreamItemDataModelType.CLIP,
            image_url = "https://cdn.primedia.co.za/primedia-broadcasting/image/fetch/h_240,w_240,c_fill,g_face/https://www.omnycontent.com/d/clips/5dcefa8e-00a9-4595-8ce1-a4ab0080f142/a07bb67a-4bc5-436e-8be2-a6cf00811fcf/9df94c92-104a-45f4-9bd5-ab8a00866189/image.jpg?t=1585211218&size=Medium",
            title = "SA Reserve Bank is buying government bonds for corona relief",
            description = "economists and financial experts discuss the unprecedent move by the SA Reserve Bank to buy government bonds for corona relief",
            media = "https://traffic.omny.fm/d/clips/5dcefa8e-00a9-4595-8ce1-a4ab0080f142/1ecba37a-6d83-4ee9-a295-a57100b7f2df/d4576f05-d601-400b-a4cb-ab89012ba680/audio.mp3"
        ),

        StreamItemDataModel(
            type = StreamItemDataModelType.CLIP,
            image_url = "https://cdn.primedia.co.za/primedia-broadcasting/image/fetch/h_240,w_240,c_fill,g_face/https://www.omnycontent.com/d/clips/5dcefa8e-00a9-4595-8ce1-a4ab0080f142/a07bb67a-4bc5-436e-8be2-a6cf00811fcf/9df94c92-104a-45f4-9bd5-ab8a00866189/image.jpg?t=1585211218&size=Medium",
            title = "What’s Viral - Elderly woman walking her dog from first floor balcony",
            description = "What’s Viral with Jonathan “Khabazela” Fairbairn",
            media = "https://traffic.omny.fm/d/clips/5dcefa8e-00a9-4595-8ce1-a4ab0080f142/2f79c727-4b1f-4cbb-8014-a6f300c285b8/b42ec575-b8ae-40a9-908b-ab880062fef6/audio.mp3"
        ),

        StreamItemDataModel(
            type = StreamItemDataModelType.CLIP,
            image_url = "https://cdn.primedia.co.za/primedia-broadcasting/image/fetch/h_240,w_240,c_fill,g_face/https://www.omnycontent.com/d/clips/5dcefa8e-00a9-4595-8ce1-a4ab0080f142/a07bb67a-4bc5-436e-8be2-a6cf00811fcf/9df94c92-104a-45f4-9bd5-ab8a00866189/image.jpg?t=1585211218&size=Medium",
            title = "What’s Viral - Woman leaving camera on while peeing during conference call",
            description = "What’s Viral with Jonathan “Khabazela” Fairbairn",
            media = "https://traffic.omny.fm/d/clips/5dcefa8e-00a9-4595-8ce1-a4ab0080f142/2f79c727-4b1f-4cbb-8014-a6f300c285b8/f17d55a2-21d0-41f4-9c38-ab870064f0c2/audio.mp3"
        ),

        StreamItemDataModel(
            type = StreamItemDataModelType.CLIP,
            image_url = "https://cdn.primedia.co.za/primedia-broadcasting/image/fetch/h_240,w_240,c_fill,g_face/https://www.omnycontent.com/d/clips/5dcefa8e-00a9-4595-8ce1-a4ab0080f142/a07bb67a-4bc5-436e-8be2-a6cf00811fcf/9df94c92-104a-45f4-9bd5-ab8a00866189/image.jpg?t=1585211218&size=Medium",
            title = "Covid-19 impact on labour",
            description = "Bongani speaks to Thobile Lamati : Director-General of Employment and Labour",
            media = "https://traffic.omny.fm/d/clips/5dcefa8e-00a9-4595-8ce1-a4ab0080f142/2f79c727-4b1f-4cbb-8014-a6f300c285b8/3cddc426-998d-4a8f-99b3-ab8a005df267/audio.mp3"
        ),

        StreamItemDataModel(
            type = StreamItemDataModelType.CLIP,
            image_url = "https://cdn.primedia.co.za/primedia-broadcasting/image/fetch/h_240,w_240,c_fill,g_face/https://www.omnycontent.com/d/clips/5dcefa8e-00a9-4595-8ce1-a4ab0080f142/a07bb67a-4bc5-436e-8be2-a6cf00811fcf/9df94c92-104a-45f4-9bd5-ab8a00866189/image.jpg?t=1585211218&size=Medium",
            title = "Shutdown is Shutdown: There will be no eHailing, Taxis and Meter taxis",
            description = "Transport Minister Fikile Mbalula takes us through some of the operational aspects in terms of private and public transport",
            media = "https://traffic.omny.fm/d/clips/5dcefa8e-00a9-4595-8ce1-a4ab0080f142/366c625b-e6db-4376-8e27-a6f300c8de6f/130a45b3-7ce8-4cec-8014-ab890073b117/audio.mp3"
        )
    )



    fun play(){
        if (currentPosition > 9){
            currentPosition = 0
        }

        player.playStream(songList[currentPosition])
        currentPosition++
    }

    fun pause(){
        player.pause()
    }

    fun isPausable(): Boolean{
        return player.isPausable()
    }

}