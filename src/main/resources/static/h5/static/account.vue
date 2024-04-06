<template>
	<view>

		<scroll-view class="content">
			<image class="bg" src="../static/bg.png"></image>

			<view class="kk">
				<view class="block">
					<image class="logo" :src="user.logo"></image>
					<view class="line">{{user.thename}} - {{user.characterName}}</view>
					<view class="line2">{{user.mobilephone}}</view>
				</view>

			</view>
			<view class="kk2">
				<view class="li" @click="gotoOffer">
					<image class="ico1" src="../static/zs.png"></image>
					<view class="label">我的报价单</view>
					<image class="ico2" src="../static/jiantou.png"></image>
				</view>
				<view class="li" @click="updatePassword">
					<image class="ico1" src="../static/lock.png"></image>
					<view class="label">修改密码</view>
					<image class="ico2" src="../static/jiantou.png"></image>
				</view>
				<view class="li" @click="logout">
					<image class="ico1" src="../static/close.png"></image>
					<view class="label">退出账号</view>
				</view>
			</view>



		</scroll-view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
			user:[]
			}
		},
		onLoad() {
			this.user = uni.getStorageSync('user');
			// console.log(uni.getStorageSync('user'));
		},
		methods: {
			logout: function() {
				let that = this;
				uni.showModal({
					title: '确认要退出吗？',
					success(res) {
						if (res.confirm) {

							that.$request('login-out', {}).then(res => {
								// let that = this;
								that.$member.v(res.data);

								uni.reLaunch({
									url: 'login'
								})

							})
						}
					}
				})

			},
			updatePassword: function() {
				uni.navigateTo({
					url: 'update-pwd'
				})
			},
			gotoOffer: function() {
				uni.navigateTo({
					url: 'offer-list'
				})
			}
		}
	}
</script>

<style>
	.line {
		margin-top: 15rpx;
		font-size: 38rpx;
		color: #000;
		font-weight: bold;
	}

	.line2 {
		margin-top: 15rpx;
	}

	.kk2 {

		position: absolute;
		z-index: 1;
		top: 420rpx;
		left: 0;
		right: 0;
		box-sizing: border-box;
		padding: 0 35rpx;
	}

	.li {
		overflow: hidden;
		background-color: #fff;
		left: 35rpx;
		right: 35rpx;
		height: 100rpx;
		margin-top: 20rpx;
		border-radius: 20rpx;
	}

	.li * {
		float: left;
	}

	.li .ico1 {
		height: 40rpx;
		width: 40rpx;
		margin: 30rpx;
	}

	.li .ico2 {
		float: right;

		height: 20rpx;
		width: 20rpx;
		margin: 40rpx;
	}

	.li .label {
		width: 50%;
		height: 100rpx;
		line-height: 100rpx;
		text-align: left;

	}

	.main {
		background-color: #333;
		/* 		background-color: #f2f4f5; */
	}

	.kk {
		padding: 0 35rpx;
		z-index: 1;
	}


	.kk .block {
		height: 250rpx;
		background-color: #fff;
		position: absolute;
		top: 150rpx;
		left: 35rpx;
		right: 35rpx;
		border-radius: 35rpx;
		text-align: center;
	}

	.logo {
		width: 200rpx;
		height: 200rpx;
		border-radius: 100rpx;
		margin: -100rpx auto 0 auto;
	}

	.bg {
		width: 100%;
		height: 800rpx;
		position: absolute;
		z-index: 0;
	}

	.content {

		padding: 0;
	}
</style>
